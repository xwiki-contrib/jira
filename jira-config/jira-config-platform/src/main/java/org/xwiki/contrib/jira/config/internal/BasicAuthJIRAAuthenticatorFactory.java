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
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.config.JIRAAuthenticator;
import org.xwiki.contrib.jira.config.JIRAuthenticatorFactory;
import org.xwiki.model.reference.LocalDocumentReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

/**
 * Factory for {@link BasicAuthJIRAAuthenticator}.
 *
 * @version $Id$
 * @since 11.0.0
 */
@Component
@Singleton
@Named(BasicAuthJIRAAuthenticatorFactory.HINT)
public class BasicAuthJIRAAuthenticatorFactory implements JIRAuthenticatorFactory
{
    /**
     * The authenticator HINT.
     */
    public static final String HINT = "basicAuth";

    private static final String JIRA = "JIRA";

    private static final String SPACE_JIRA_AUTH = "JIRAAuth";

    private static final String CONFIG_ID_FIELD = "id";

    /**
     * The document for storing the basic auth configuration.
     */
    public static final LocalDocumentReference BASIC_AUTH_CONFIG_REFERENCE =
        new LocalDocumentReference(List.of(JIRA, SPACE_JIRA_AUTH), "BasicAuthConfiguration");

    /**
     * The class reference for the basic auth configuration.
     */
    public static final LocalDocumentReference BASIC_AUTH_DATA_CLASS_REFERENCE =
        new LocalDocumentReference(List.of(JIRA, SPACE_JIRA_AUTH), "BasicAuthClass");

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Override
    public JIRAAuthenticator get(String serverId) throws JIRAAuthenticatorException
    {
        XWikiContext context = contextProvider.get();
        XWikiDocument doc;
        try {
            doc = context.getWiki().getDocument(BASIC_AUTH_CONFIG_REFERENCE, context);
        } catch (XWikiException e) {
            throw new JIRAAuthenticatorException("Can't get JIRA Basic auth configuration document", e);
        }
        Optional<BaseObject> authObj = doc.getXObjects(BASIC_AUTH_DATA_CLASS_REFERENCE)
            .stream().filter(x -> StringUtils.equals(serverId, (x.getStringValue(CONFIG_ID_FIELD))))
            .findFirst();
        if (authObj.isPresent()) {
            String username = authObj.get().getStringValue("username");
            String password = authObj.get().getStringValue("password");
            return new BasicAuthJIRAAuthenticator(username, password);
        } else {
            throw new JIRAAuthenticatorException("Can't find Basic auth config for server ID: " + serverId);
        }
    }
}
