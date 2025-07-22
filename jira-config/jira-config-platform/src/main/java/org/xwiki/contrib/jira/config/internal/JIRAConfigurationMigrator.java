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
import org.xwiki.component.annotation.Component;
import org.xwiki.model.document.DocumentAuthors;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.query.QueryManager;
import org.xwiki.user.UserReference;
import org.xwiki.user.internal.document.DocumentUserReference;

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
@Component(roles = { JIRAConfigurationMigrator.class })
public class JIRAConfigurationMigrator
{
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
    private Provider<QueryManager> queryManagerProvider;

    @Inject
    @Named("currentmixed")
    private DocumentReferenceResolver<String> currentMixedReferenceResolver;

    @Inject
    private Logger logger;

    /**
     * Run the data migration for JIRA server config.
     */
    public void migrate()
    {
        XWikiContext context = contextProvider.get();
        XWiki xwiki = context.getWiki();
        DocumentReference migrationDocumentRef =
            new DocumentReference(MIGRATION_STATUS_DOCUMENT_REFERENCE, new WikiReference(context.getWikiId()));

        try {
            if (!xwiki.exists(migrationDocumentRef, context)) {
                logger.info("Running migration of JIRA configuration");

                XWikiDocument basicAuthDoc =
                    xwiki.getDocument(BasicAuthJIRAAuthenticatorFactory.BASIC_AUTH_CONFIG_REFERENCE, context).clone();
                XWikiDocument jiraServerDoc =
                    xwiki.getDocument(new LocalDocumentReference(JIRA, "JIRAConfig"), context).clone();
                List<BaseObject> jiraServerObjs =
                    jiraServerDoc.getXObjects(new LocalDocumentReference(JIRA, "JIRAConfigClass"));
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

        XWikiDocument migrationStateDoc = xwiki.getDocument(migrationDocumentRef, context).clone();
        UserReference userRef = new DocumentUserReference(
            new DocumentReference("xwiki", "XWiki", "superadmin"), true);
        DocumentAuthors authors = migrationStateDoc.getAuthors();
        authors.setContentAuthor(userRef);
        authors.setEffectiveMetadataAuthor(userRef);
        authors.setOriginalMetadataAuthor(userRef);
        xwiki.saveDocument(migrationStateDoc, context);
    }
}
