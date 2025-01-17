package org.codehaus.mojo.gwt.shell;

/*
 * I18NMojo.java
 *
 * Created on August 19th, 2008
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Creates I18N interfaces for constants and messages files.
 *
 * @goal i18n
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @description Creates I18N interfaces for constants and messages files.
 * @author Sascha-Matthias Kulawik <sascha@kulawik.de>
 * @author ccollins
 * @version $Id$
 */
public class I18NMojo
    extends AbstractGwtShellMojo
{
    /**
     * List of resourceBundles that should be used to generate i18n Messages interfaces.
     *
     * @parameter
     * @alias i18nMessagesNames
     */
    private String[] i18nMessagesBundles;

    /**
     * Shortcut for a single i18nMessagesBundle
     *
     * @parameter
     */
    private String i18nMessagesBundle;

    /**
     * List of resourceBundles that should be used to generate i18n Constants interfaces.
     *
     * @parameter
     * @alias i18nConstantsNames
     */
    private String[] i18nConstantsBundles;

    /**
     * Shortcut for a single i18nConstantsBundle
     *
     * @parameter
     */
    private String i18nConstantsBundle;

    /**
     * List of resourceBundles that should be used to generate i18n ConstantsWithLookup interfaces.
     *
     * @parameter
     */
    private String[] i18nConstantsWithLookupBundles;

    /**
     * Shortcut for a single i18nConstantsWithLookupBundle
     *
     * @parameter
     */
    private String i18nConstantsWithLookupBundle;

    public void doExecute( )
        throws MojoExecutionException, MojoFailureException
    {
        setup();
        boolean generated = false;

        // constants with lookup
        if ( i18nConstantsWithLookupBundles != null )
        {
            for ( String target : i18nConstantsWithLookupBundles )
            {
                File targetFile = buildTargetFile( getGenerateDirectory(), target );
                getLog().info( "Generating " + targetFile.toString() + ".java" );
                ensureTargetPackageExists( targetFile );
                new JavaCommand( "com.google.gwt.i18n.tools.I18NSync" ).withinScope( Artifact.SCOPE_COMPILE )
                    .withinClasspath( getGwtUserJar() ).withinClasspath( getGwtDevJar() )
                    .arg( "-out", getGenerateDirectory().getAbsolutePath() ).arg( "-createConstantsWithLookup" )
                    .arg( target ).execute();
                generated = true;
            }
        }

        // constants
        if ( i18nConstantsBundles != null )
        {
            for ( String target : i18nConstantsBundles )
            {
                File targetFile = buildTargetFile( getGenerateDirectory(), target );
                getLog().info( "Generating " + targetFile.toString() + ".java" );
                ensureTargetPackageExists( targetFile );
                new JavaCommand( "com.google.gwt.i18n.tools.I18NSync" ).withinScope( Artifact.SCOPE_COMPILE )
                    .withinClasspath( getGwtUserJar() ).withinClasspath( getGwtDevJar() )
                    .arg( "-out", getGenerateDirectory().getAbsolutePath() ).arg( target ).execute();
                generated = true;
            }
        }

        // messages
        if ( i18nMessagesBundles != null )
        {
            for ( String target : i18nMessagesBundles )
            {
                File targetFile = buildTargetFile( getGenerateDirectory(), target );
                getLog().info( "Generating " + targetFile.toString() + ".java" );
                ensureTargetPackageExists( targetFile );
                new JavaCommand( "com.google.gwt.i18n.tools.I18NSync" ).withinScope( Artifact.SCOPE_COMPILE )
                    .withinClasspath( getGwtUserJar() ).withinClasspath( getGwtDevJar() )
                    .arg( "-out", getGenerateDirectory().getAbsolutePath() ).arg( "-createMessages" ).arg( target )
                    .execute();
                generated = true;
            }
        }

        if ( generated )
        {
            getLog().debug( "add compile source root " + getGenerateDirectory() );
            addCompileSourceRoot( getGenerateDirectory() );
        }
    }


    private void setup()
        throws MojoExecutionException
    {
        if ( i18nConstantsWithLookupBundles == null && i18nConstantsWithLookupBundle != null )
        {
            i18nConstantsWithLookupBundles = new String[] { i18nConstantsWithLookupBundle };
        }

        if ( i18nConstantsBundles == null && i18nConstantsBundle != null )
        {
            i18nConstantsBundles = new String[] { i18nConstantsBundle };
        }

        if ( i18nMessagesBundles == null && i18nMessagesBundle != null )
        {
            i18nMessagesBundles = new String[] { i18nMessagesBundle };
        }
    }


    private File buildTargetFile( File generateDirectory, String targetName )
    {
        return new File( generateDirectory, targetName.replace( '.', File.separatorChar ) );
    }

    private void ensureTargetPackageExists( File targetFile )
    {
        getLog().debug( "ensureTargetPackageExists, targetFile : " + targetFile );
        if ( !targetFile.getParentFile().exists() )
        {
            targetFile.getParentFile().mkdirs();
        }
    }

}
