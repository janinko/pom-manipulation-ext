/**
 * Copyright (C) 2012 Red Hat, Inc. (jcasey@redhat.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.maven.ext.manip.state;

import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;
import org.commonjava.maven.ext.manip.impl.PluginManipulator;
import org.commonjava.maven.ext.manip.util.IdUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Captures configuration relating to plugin alignment from the POMs. Used by {@link PluginManipulator}.
 */
public class PluginState
    implements State
{
    /**
     * The name of the property which contains the GAV of the remote pom from which to retrieve plugin management
     * information.
     * <pre>
     * <code>-DpluginManagement:org.foo:bar-plugin-mgmt:1.0</code>
     * </pre>
     */
    private static final String PLUGIN_MANAGEMENT_POM_PROPERTY = "pluginManagement";

    /**
     * Two possible methods currently supported configuration merging precedence:
     * <pre>
     * <code>REMOTE</code> (default)
     * <code>LOCAL</code>
     * </pre>
     * Configured by the property <code>-DpluginManagementPrecedence=[REMOTE|LOCAL]</code>
     */
    public static enum Precedence
    {
        REMOTE,
        LOCAL;
    }

    private final List<ProjectVersionRef> pluginMgmt;

    private final Precedence configPrecedence;

    /**
     * Used to store mappings of old property to new version.
     */
    private final Map<String, String> versionPropertyUpdateMap = new HashMap<String, String>();

    public PluginState( final Properties userProps )
    {
        pluginMgmt = IdUtils.parseGAVs( userProps.getProperty( PLUGIN_MANAGEMENT_POM_PROPERTY ) );
        switch ( Precedence.valueOf( userProps.getProperty( "pluginManagementPrecedence",
                                                            Precedence.REMOTE.toString() ).toUpperCase() ) )
        {
            case LOCAL:
            {
                configPrecedence = Precedence.LOCAL;
                break;
            }
            case REMOTE:
            default:
            {
                configPrecedence = Precedence.REMOTE;
                break;
            }
        }
    }

    /**
     * Enabled ONLY if propertyManagement is provided in the user properties / CLI -D options.
     *
     * @see org.commonjava.maven.ext.manip.state.State#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return pluginMgmt != null && !pluginMgmt.isEmpty();
    }

    public List<ProjectVersionRef> getRemotePluginMgmt()
    {
        return pluginMgmt;
    }

    public void addVersionPropertyOverride( String oldVersionProp, String version )
    {
        versionPropertyUpdateMap.put( oldVersionProp, version );
    }

    public String getVersionPropertyOverride( String prop )
    {
        return versionPropertyUpdateMap.get( prop );
    }

    public Map<String, String> getVersionPropertyOverrides()
    {
        return versionPropertyUpdateMap;
    }

    public Precedence getConfigPrecedence()
    {
        return configPrecedence;
    }

}
