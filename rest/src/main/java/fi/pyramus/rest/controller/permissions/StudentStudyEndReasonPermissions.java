package fi.pyramus.rest.controller.permissions;

import java.util.List;

import fi.muikku.security.Scope;
import fi.pyramus.security.impl.AbstractPyramusPermissionCollection;
import fi.pyramus.security.impl.DefaultPermissionRoles;
import fi.pyramus.security.impl.PermissionScope;
import fi.pyramus.security.impl.PyramusPermissionCollection;

public class StudentStudyEndReasonPermissions extends AbstractPyramusPermissionCollection implements PyramusPermissionCollection {

  @Scope (PermissionScope.ENVIRONMENT)
  @DefaultPermissionRoles ({ ADMINISTRATOR, MANAGER })
  public static final String CREATE_STUDENTSTUDYENDREASON = "CREATE_STUDENTSTUDYENDREASON";
  
  @Scope (PermissionScope.ENVIRONMENT)
  @DefaultPermissionRoles ({ ADMINISTRATOR, MANAGER, USER, GUEST, STUDENT })
  public static final String LIST_STUDENTSTUDYENDREASONS = "LIST_STUDENTSTUDYENDREASONS";
  
  @Scope (PermissionScope.ENVIRONMENT)
  @DefaultPermissionRoles ({ ADMINISTRATOR, MANAGER, USER, GUEST, STUDENT })
  public static final String FIND_STUDENTSTUDYENDREASON = "FIND_STUDENTSTUDYENDREASON";
  
  @Scope (PermissionScope.ENVIRONMENT)
  @DefaultPermissionRoles ({ ADMINISTRATOR, MANAGER })
  public static final String UPDATE_STUDENTSTUDYENDREASON = "UPDATE_STUDENTSTUDYENDREASON";
  
  @Scope (PermissionScope.ENVIRONMENT)
  @DefaultPermissionRoles ({ ADMINISTRATOR })
  public static final String DELETE_STUDENTSTUDYENDREASON = "DELETE_STUDENTSTUDYENDREASON";
  
  
  
  @Override
  public List<String> listPermissions() {
    return super.listPermissions(StudentStudyEndReasonPermissions.class);
  }

  @Override
  public boolean containsPermission(String permission) {
    return listPermissions().contains(permission);
  }

  @Override
  public String getPermissionScope(String permission) throws NoSuchFieldException {
    return super.getPermissionScope(StudentStudyEndReasonPermissions.class, permission);
  }

  @Override
  public String[] getDefaultRoles(String permission) throws NoSuchFieldException {
    return super.getDefaultRoles(StudentStudyEndReasonPermissions.class, permission);
  }

}
