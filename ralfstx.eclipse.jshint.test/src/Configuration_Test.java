import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ralfstx.eclipse.jshint.Configuration;


public class Configuration_Test {

  private Configuration configuration;

  @Before
  public void setUp() {
    configuration = new Configuration();
  }

  @Test
  public void emptyAfterCreation() throws Exception {
    assertEquals( "{}", configuration.getOptionsString() );
  }

@Test
  public void addOneOption() throws Exception {
    configuration.addOption( "foo", true );

    assertEquals( "{\"foo\": true}", configuration.getOptionsString() );
  }

  @Test
public void addSeparateOptions() throws Exception {
  configuration.addOption( "foo", true );
  configuration.addOption( "bar", false );

  assertEquals( "{\"foo\": true, \"bar\": false}", configuration.getOptionsString() );
}

  @Test( expected=IllegalArgumentException.class )
public void addSameOptionTwice() throws Exception {
  configuration.addOption( "foo", true );
  configuration.addOption( "foo", true );
}

  @Test( expected=IllegalArgumentException.class )
  public void addSameGlobalTwice() throws Exception {
    configuration.addGlobal( "foo", true );
    configuration.addGlobal( "foo", true );
  }

  @Test
  public void addOneGlobal() throws Exception {
    configuration.addGlobal( "foo", true );

    assertEquals( "{\"predef\": {\"foo\": true}}", configuration.getOptionsString() );
  }

  @Test
  public void addSeparateGlobals() throws Exception {
    configuration.addGlobal( "foo", true );
    configuration.addGlobal( "bar", false );

    assertEquals( "{\"predef\": {\"foo\": true, \"bar\": false}}", configuration.getOptionsString() );
  }

  @Test
  public void addGlobalAndOption() throws Exception {
    configuration.addGlobal( "foo", true );
    configuration.addOption( "bar", false );

    assertEquals( "{\"predef\": {\"foo\": true}, \"bar\": false}", configuration.getOptionsString() );
  }

}
