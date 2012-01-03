package ralfstx.eclipse.jshint.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import ralfstx.eclipse.jshint.Activator;


public class StatusHelper {

  private static final String QUALIFIER = Activator.PLUGIN_ID;
  private static final QualifiedName PROJECT_ENABLED = new QualifiedName( QUALIFIER, "projEnabled" );
  private static final QualifiedName FILE_EXCLUDED = new QualifiedName( QUALIFIER, "fileExcluded" );

  public static boolean getProjectEnabled( IResource resource ) throws CoreException {
    return "true".equals( resource.getProject().getPersistentProperty( PROJECT_ENABLED ) );
  }

  public static boolean getFileExcluded( IResource resource ) throws CoreException {
    return "true".equals( resource.getPersistentProperty( FILE_EXCLUDED ) );
  }

  public static void setProjectEnabled( IResource resource, boolean enabled ) throws CoreException {
    resource.getProject().setPersistentProperty( PROJECT_ENABLED, Boolean.toString( enabled ) );
  }

  public static void setFileExcluded( IResource resource, boolean excluded ) throws CoreException {
    resource.setPersistentProperty( FILE_EXCLUDED, Boolean.toString( excluded ) );
  }

}
