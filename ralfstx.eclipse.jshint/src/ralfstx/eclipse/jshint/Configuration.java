package ralfstx.eclipse.jshint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Configuration {

  private static Map<String, Boolean> defaults = initDefaults();

  private HashMap<String, Boolean> options;

  public Configuration() {
    options = new HashMap<String, Boolean>( defaults );
  }

  public void set( String option, boolean value ) {
    if( !defaults.containsKey( option ) ) {
      throw new IllegalArgumentException( "Unknown option: " + option );
    }
    defaults.put( option, Boolean.valueOf( value ) );
  }

  public Set<String> getOptions() {
    return Collections.unmodifiableSet( defaults.keySet() );
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append( "{\n" );
    for( String option : options.keySet() ) {
      builder.append( "  " );
      builder.append( option );
      builder.append( ": " );
      builder.append( options.get( option ) );
      builder.append( ",\n" );
    }
    builder.append( "}" );
    return builder.toString();
  }

  private static Map<String, Boolean> initDefaults() {
    HashMap<String, Boolean> result = new HashMap<String, Boolean>();
    result.put( "bitwise", Boolean.TRUE );
    result.put( "curly", Boolean.TRUE );
    result.put( "eqeqeq", Boolean.TRUE );
    result.put( "forin", Boolean.TRUE );
    result.put( "immed", Boolean.TRUE );
    result.put( "latedef", Boolean.TRUE );
    result.put( "newcap", Boolean.TRUE );
    result.put( "noarg", Boolean.TRUE );
    result.put( "noempty", Boolean.TRUE );
    result.put( "nonew", Boolean.TRUE );
    result.put( "plusplus", Boolean.TRUE );
    result.put( "regexp", Boolean.TRUE );
    result.put( "undef", Boolean.TRUE );
    result.put( "strict", Boolean.TRUE );
    result.put( "trailing", Boolean.TRUE );
    // RELAXING OPTIONS
    result.put( "asi", Boolean.FALSE );
    result.put( "boss", Boolean.FALSE );
    result.put( "debug", Boolean.FALSE );
    result.put( "eqnull", Boolean.FALSE );
    result.put( "es5", Boolean.FALSE );
    result.put( "esnext", Boolean.FALSE );
    result.put( "evil", Boolean.FALSE );
    result.put( "expr", Boolean.FALSE );
    result.put( "funcscope", Boolean.FALSE );
    result.put( "globalstrict", Boolean.FALSE );
    result.put( "iterator", Boolean.FALSE );
    result.put( "lastsemic", Boolean.FALSE );
    result.put( "laxbreak", Boolean.FALSE );
    result.put( "loopfunc", Boolean.FALSE );
    result.put( "multistr", Boolean.FALSE );
    result.put( "onecase", Boolean.FALSE );
    result.put( "proto", Boolean.FALSE );
    result.put( "regexdash", Boolean.FALSE );
    result.put( "scripturl", Boolean.FALSE );
    result.put( "smarttabs", Boolean.FALSE );
    result.put( "shadow", Boolean.FALSE );
    result.put( "sub", Boolean.FALSE );
    result.put( "supernew", Boolean.FALSE );
    result.put( "validthis", Boolean.FALSE );
    // ENVIRONMENTS
    result.put( "browser", Boolean.FALSE );
    result.put( "couch", Boolean.FALSE );
    result.put( "devel", Boolean.FALSE );
    result.put( "dojo", Boolean.FALSE );
    result.put( "jquery", Boolean.FALSE );
    result.put( "mootools", Boolean.FALSE );
    result.put( "node", Boolean.FALSE );
    result.put( "nonstandard", Boolean.FALSE );
    result.put( "prototypejs", Boolean.FALSE );
    result.put( "rhino", Boolean.FALSE );
    result.put( "wsh", Boolean.FALSE );
    return result;
  }

}
