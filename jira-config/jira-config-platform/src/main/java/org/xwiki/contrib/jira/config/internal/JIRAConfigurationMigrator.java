/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.jira.config.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.xwiki.bridge.event.ApplicationReadyEvent;
import org.xwiki.bridge.event.WikiReadyEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.namespace.NamespaceUtils;
import org.xwiki.extension.job.internal.InstallJob;
import org.xwiki.job.event.JobFinishedEvent;
import org.xwiki.model.document.DocumentAuthors;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.observation.AbstractEventListener;
import org.xwiki.observation.event.Event;
import org.xwiki.user.SuperAdminUserReference;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

/**
 * Listener to handle migration of authentication in JIRA configuration.
 *
 * @version $Id$
 * @since 11.0.0
 */
@Singleton
@Component
@Named(JIRAConfigurationMigrator.HINT)
public class JIRAConfigurationMigrator extends AbstractEventListener
{
    /**
     * Main name of the listener.
     */
    public static final String HINT = "JIRAConfigMigrationListener";

    private static final String JIRA = "JIRA";

    private static final String PROPERTY_USERNAME = "username";

    private static final String PROPERTY_PASSWORD = "password";

    private static final String PROPERTY_ID = "id";

    private static final String PROPERTY_AUTHENTICATION_TYPE = "authenticationType";

    private static final String DOC_SAVE_MESSAGE = "Migrate configration";

    private static final LocalDocumentReference MIGRATION_STATUS_DOCUMENT_REFERENCE =
        new LocalDocumentReference(JIRA, "JIRAConfigAuthMigration");

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private Logger logger;

    /**
     * Public constructor.
     */
    public JIRAConfigurationMigrator()
    {
        super(HINT, List.of(new ApplicationReadyEvent(), new WikiReadyEvent(), new JobFinishedEvent("install")));
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        if (source instanceof InstallJob) {
            // The JobFinishedEvent event are allways called with the context set to the main wiki, so we need to get
            // the wiki ID in a different way.
            for (String namespace : ((InstallJob) source).getRequest().getNamespaces()) {
                String wikiId = NamespaceUtils.toNamespace(namespace).getValue();
                migrate(wikiId);
            }
        } else {
            XWikiContext context = contextProvider.get();
            migrate(context.getWikiId());
        }
    }

    private void migrate(String wikiId)
    {
        XWikiContext context = contextProvider.get();
        XWiki xwiki = context.getWiki();
        WikiReference wikiReference = new WikiReference(wikiId);
        DocumentReference migrationDocumentRef =
            new DocumentReference(MIGRATION_STATUS_DOCUMENT_REFERENCE, wikiReference);

        try {
            if (!xwiki.exists(migrationDocumentRef, context)) {
                logger.info("Running migration of JIRA configuration");

                DocumentReference basicAuthConfigRef =
                    new DocumentReference(BasicAuthJIRAAuthenticatorFactory.BASIC_AUTH_CONFIG_REFERENCE, wikiReference);
                XWikiDocument basicAuthDoc = xwiki.getDocument(basicAuthConfigRef, context).clone();
                XWikiDocument jiraServerDoc =
                    xwiki.getDocument(new DocumentReference(wikiId, JIRA, "JIRAConfig"), context).clone();
                List<BaseObject> jiraServerObjs =
                    jiraServerDoc.getXObjects(new DocumentReference(wikiId, JIRA, "JIRAConfigClass"));
                for (BaseObject obj : jiraServerObjs) {
                    if (obj == null) {
                        continue;
                    }
                    String serverId = obj.getStringValue(PROPERTY_ID);
                    String username = obj.getStringValue(PROPERTY_USERNAME);
                    String password = obj.getStringValue(PROPERTY_PASSWORD);

                    BaseObject jiraServerObj = jiraServerObjs.stream()
                        .filter(o -> o != null && StringUtils.equals(o.getStringValue(PROPERTY_ID), serverId))
                        .findFirst().orElseThrow();
                    jiraServerObj.removeField(PROPERTY_USERNAME);
                    jiraServerObj.removeField(PROPERTY_PASSWORD);

                    if (StringUtils.isEmpty(username)) {
                        // server without authentication
                        jiraServerObj.setStringValue(PROPERTY_AUTHENTICATION_TYPE, "noAuth");
                    } else {
                        jiraServerObj.setStringValue(PROPERTY_AUTHENTICATION_TYPE,
                            BasicAuthJIRAAuthenticatorFactory.HINT);
                        BaseObject basicAuthObj = basicAuthDoc
                            .newXObject(BasicAuthJIRAAuthenticatorFactory.BASIC_AUTH_DATA_CLASS_REFERENCE, context);
                        basicAuthObj.setStringValue(PROPERTY_ID, serverId);
                        basicAuthObj.setStringValue(PROPERTY_USERNAME, username);
                        basicAuthObj.setStringValue(PROPERTY_PASSWORD, password);
                    }
                }
                xwiki.saveDocument(basicAuthDoc, DOC_SAVE_MESSAGE, context);
                xwiki.saveDocument(jiraServerDoc, DOC_SAVE_MESSAGE, context);

                // Juste create a single document to save the fact that we run the migration
                createMigrationStateDocument();
            }
        } catch (XWikiException e) {
            logger.error("Can't handle migration of JIRA server configuration", e);
        }
    }

    private void createMigrationStateDocument()
        throws XWikiException
    {
        XWikiContext context = contextProvider.get();
        XWiki xwiki = context.getWiki();
        DocumentReference migrationDocumentRef =
            new DocumentReference(MIGRATION_STATUS_DOCUMENT_REFERENCE, new WikiReference(context.getWikiId()));

        XWikiDocument migrationStateDoc = xwiki.getDocument(migrationDocumentRef, context);
        migrationStateDoc.setHidden(true);
        migrationStateDoc.setContent(
            "Dummy document to store the fact that the JIRA configuration migration was executed. "
                + "This document avoid to execute again the migration");
        DocumentAuthors authors = migrationStateDoc.getAuthors();
        authors.setContentAuthor(SuperAdminUserReference.INSTANCE);
        authors.setEffectiveMetadataAuthor(SuperAdminUserReference.INSTANCE);
        authors.setOriginalMetadataAuthor(SuperAdminUserReference.INSTANCE);
        xwiki.saveDocument(migrationStateDoc, context);
    }
}
