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
package org.xwiki.localization;

/**
 * Minimal stand-in for the XWiki Platform role of the same name, which this module cannot depend on. It lets the tests
 * exercise the optional lookup done by the key displayer.
 *
 * @version $Id$
 */
public interface ContextualLocalizationManager
{
    /**
     * @param key the translation key
     * @param parameters the translation parameters
     * @return the translation, or {@code null} when there is none
     */
    String getTranslationPlain(String key, Object... parameters);
}
