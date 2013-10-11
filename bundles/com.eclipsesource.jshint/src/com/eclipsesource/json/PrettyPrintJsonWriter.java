/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.json;

import java.io.IOException;
import java.io.Writer;


public class PrettyPrintJsonWriter extends JsonWriter {

  public PrettyPrintJsonWriter( Writer writer ) {
    super( writer );
  }

  private int indent = 0;

  @Override
  protected void writeBeginObject() throws IOException {
    super.writeBeginObject();
    indent++;
    writeNewline();
  }

  @Override
  protected void writeEndObject() throws IOException {
    indent--;
    writeNewline();
    super.writeEndObject();
  }

  @Override
  protected void writeNameValueSeparator() throws IOException {
    super.writeNameValueSeparator();
    writer.write( ' ' );
  }

  @Override
  protected void writeObjectValueSeparator() throws IOException {
    super.writeObjectValueSeparator();
    writeNewline();
  }

  @Override
  protected void writeArrayValueSeparator() throws IOException {
    super.writeArrayValueSeparator();
    writer.write( ' ' );
  }

  protected void writeNewline() throws IOException {
    writer.write( '\n' );
    for( int i = 0; i < indent; i++ ) {
      writer.write( "  " );
    }
  }

}
