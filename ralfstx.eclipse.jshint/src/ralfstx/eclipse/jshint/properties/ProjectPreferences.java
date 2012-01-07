package ralfstx.eclipse.jshint.properties;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import ralfstx.eclipse.jshint.Activator;
import ralfstx.eclipse.jshint.Configuration;


public class ProjectPreferences {

  private static final String KEY_GLOBALS = "globals";
  private static final String KEY_OPTIONS = "options";
  private static final String DEF_GLOBALS = "";
  private static final String DEF_OPTIONS = "";

  private final IEclipsePreferences node;

  public ProjectPreferences( IProject project ) {
    this.node = new ProjectScope( project ).getNode( Activator.PLUGIN_ID );
  }

  public IEclipsePreferences getNode() {
    return node;
  }

  public String getGlobals() {
    return node.get( KEY_GLOBALS, DEF_GLOBALS );
  }

  public void setGlobals( String value ) {
    if( DEF_GLOBALS.equals( value ) ) {
      node.remove( KEY_GLOBALS );
    } else {
      node.put( KEY_GLOBALS, value );
    }
  }

  public String getOptions() {
    return node.get( KEY_OPTIONS, DEF_OPTIONS );
  }

  public void setOptions( String value ) {
    if( value.equals( DEF_OPTIONS ) ) {
      node.remove( KEY_OPTIONS );
    } else {
      node.put( KEY_OPTIONS, value );
    }
  }

  public void save() throws CoreException {
    try {
      node.flush();
    } catch( BackingStoreException exception ) {
      String message = "Failed to store preferences";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

  public Configuration getConfiguration() {
    Configuration configuration = new Configuration();
    for( OptionParserUtil.Entry entry : OptionParserUtil.parseOptionString( getGlobals() ) ) {
      configuration.addGlobal( entry.name, entry.value );
    }
    for( OptionParserUtil.Entry entry : OptionParserUtil.parseOptionString( getOptions() ) ) {
      configuration.addOption( entry.name, entry.value );
    }
    return configuration;
  }

}
