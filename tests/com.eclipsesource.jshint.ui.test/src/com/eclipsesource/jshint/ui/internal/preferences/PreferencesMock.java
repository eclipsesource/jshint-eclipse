package com.eclipsesource.jshint.ui.internal.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public class PreferencesMock implements Preferences {

  private final String name;
  private final Map<String, Object> values;

  public PreferencesMock( String name ) {
    this.name = name;
    values = new HashMap<String, Object>();
  }

  public void put( String key, String value ) {
    values.put( key, value );
  }

  public String get( String key, String def ) {
    String result = (String)values.get( key );
    return result != null ? result : def;
  }

  public void remove( String key ) {
    values.remove( key );
  }

  public void clear() throws BackingStoreException {
    values.clear();
  }

  public void putInt( String key, int value ) {
    values.put( key, Integer.valueOf( value ) );
  }

  public int getInt( String key, int def ) {
    Integer result = (Integer)values.get( key );
    return result != null ? result.intValue() : def;
  }

  public void putLong( String key, long value ) {
    values.put( key, Long.valueOf( value ) );
  }

  public long getLong( String key, long def ) {
    Long result = (Long)values.get( key );
    return result != null ? result.longValue() : def;
  }

  public void putBoolean( String key, boolean value ) {
    values.put( key, Boolean.valueOf( value ) );

  }

  public boolean getBoolean( String key, boolean def ) {
    Boolean result = (Boolean)values.get( key );
    return result != null ? result.booleanValue() : def;
  }

  public void putFloat( String key, float value ) {
    values.put( key, Float.valueOf( value ) );
  }

  public float getFloat( String key, float def ) {
    Float result = (Float)values.get( key );
    return result != null ? result.floatValue() : def;
  }

  public void putDouble( String key, double value ) {
    values.put( key, Double.valueOf( value ) );
  }

  public double getDouble( String key, double def ) {
    Double result = (Double)values.get( key );
    return result != null ? result.doubleValue() : def;
  }

  public void putByteArray( String key, byte[] value ) {
    values.put( key, value );
  }

  public byte[] getByteArray( String key, byte[] def ) {
    byte[] result = (byte[])values.get( key );
    return result != null ? result : def;
  }

  public String[] keys() throws BackingStoreException {
    Set<String> keys = values.keySet();
    return keys.toArray( new String[ values.size() ] );
  }

  public String[] childrenNames() throws BackingStoreException {
    return new String[ 0 ];
  }

  public Preferences parent() {
    return null;
  }

  public Preferences node( String pathName ) {
    throw new UnsupportedOperationException();
  }

  public boolean nodeExists( String pathName ) throws BackingStoreException {
    return false;
  }

  public void removeNode() throws BackingStoreException {
    throw new UnsupportedOperationException();
  }

  public String name() {
    return name;
  }

  public String absolutePath() {
    throw new UnsupportedOperationException();
  }

  public void flush() throws BackingStoreException {
    // do nothing
  }

  public void sync() throws BackingStoreException {
    // do nothing
  }

}
