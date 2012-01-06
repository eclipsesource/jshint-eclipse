package ralfstx.eclipse.jshint;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * See http://www.jshint.com/options/
 */
public class Configuration {

  private final Map<String, Boolean> options;
  private final Map<String,Boolean> globals;

  public Configuration() {
    globals = new LinkedHashMap<String, Boolean>();
    options = new LinkedHashMap<String, Boolean>();
  }

  public void addOption( String option, boolean value ) {
    if( options.containsKey( option ) ) {
      throw new IllegalArgumentException( "Duplicate option: " + option );
    }
    options.put( option, Boolean.valueOf( value ) );
  }

  public void addGlobal( String identifier, boolean overwrite ) {
    if( globals.containsKey( identifier ) ) {
      throw new IllegalArgumentException( "Duplicate global identifier: " + identifier );
    }
    globals.put( identifier, Boolean.valueOf( overwrite ) );
  }

  public String getOptionsString() {
    StringBuilder builder = new StringBuilder();
    builder.append( "{" );
    if( !globals.isEmpty() ) {
      builder.append( "\"predef\": {" );
      addMap( builder, globals );
      builder.append( "}" );
      if( !options.isEmpty() ) {
        builder.append( ", " );
      }
    }
    addMap( builder, options );
    builder.append( "}" );
    return builder.toString();
  }

  private void addMap( StringBuilder builder, Map<String, Boolean> map ) {
    boolean first = true;
    for( String key : map.keySet() ) {
      if( !first ) {
        builder.append( ", " );
      }
      builder.append( '"' );
      builder.append( key );
      builder.append( "\": " );
      builder.append( map.get( key ) );
      first = false;
    }
  }

}
