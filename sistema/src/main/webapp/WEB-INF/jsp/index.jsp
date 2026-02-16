<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <jsp:include page="/WEB-INF/jsp/head.jsp"/>
</head>

<body id="page-top">

    <!-- Page Wrapper -->
    <div id="wrapper">
    <jsp:include page="/WEB-INF/jsp/nav_bar.jsp"/>


        <!-- Content Wrapper -->
        <div id="content-wrapper" class="d-flex flex-column">

            <!-- Main Content -->
            <div id="content">

                <!-- Topbar -->
                <nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">

                    <!-- Sidebar Toggle (Topbar) -->
                    <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
                        <i class="fa fa-bars"></i>
                    </button>

                    <!-- Topbar Navbar -->
                    <ul class="navbar-nav ml-auto">

                        <!-- Nav Item - Search Dropdown (Visible Only XS) -->
                        <li class="nav-item dropdown no-arrow d-sm-none">


                        </li>

                        <div class="topbar-divider d-none d-sm-block"></div>

                        <!-- Nav Item - User Information -->
                        <li class="nav-item dropdown no-arrow">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <span class="mr-2 d-none d-lg-inline text-gray-600 small">
                                            <sec:authentication property="principal.username"/>

                                            <!-- Badge según el rol -->
                                            <sec:authorize access="hasRole('ADMIN')">
                                                <span class="badge badge-danger ml-1">ADMIN</span>
                                            </sec:authorize>

                                            <sec:authorize access="hasRole('EMPLEADO') and !hasRole('ADMIN')">
                                                <span class="badge badge-primary ml-1">EMPLEADO</span>
                                            </sec:authorize>

                                        </span>
                                <img class="img-profile rounded-circle"
                                    src="${pageContext.request.contextPath}/img/undraw_profile.svg">
                            </a>
                            <!-- Dropdown - User Information -->
                            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                                aria-labelledby="userDropdown">

                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="#" onclick="document.getElementById('logoutForm').submit();">
                                    <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Cerrar Sesión
                                </a>
                            </div>
                        </li>

                    </ul>

                    <form id="logoutForm" method="POST" action="${pageContext.request.contextPath}/logout" style="display:none;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>

                </nav>
                <!-- End of Topbar -->

                <!-- Begin Page Content -->
                <div class="container-fluid">

                    <!-- Page Heading -->
                    <div class="d-sm-flex align-items-center justify-content-between mb-4">
                        <h1 class="h3 mb-0 text-gray-800">Menú</h1>
                    </div>

                   <!-- Fila 1: Hoy -->
                   <div class="row">
                       <!-- Ventas Hoy -->
                       <div class="col-xl-4 col-md-6 mb-4">
                           <div class="card border-left-primary shadow h-100 py-2">
                               <div class="card-body">
                                   <div class="row no-gutters align-items-center">
                                       <div class="col mr-2">
                                           <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                               Ventas Hoy
                                           </div>
                                           <div class="h5 mb-0 font-weight-bold text-gray-800">${ventasDia}</div>
                                       </div>
                                       <div class="col-auto">
                                           <i class="fas fa-calendar fa-2x text-gray-300"></i>
                                       </div>
                                   </div>
                               </div>
                           </div>
                       </div>

                       <!-- Ganancia Hoy -->
                       <div class="col-xl-4 col-md-6 mb-4">
                           <div class="card border-left-success shadow h-100 py-2">
                               <div class="card-body">
                                   <div class="row no-gutters align-items-center">
                                       <div class="col mr-2">
                                           <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                               Ganancia Hoy
                                           </div>
                                           <div class="h5 mb-0 font-weight-bold text-gray-800">${gananciaDia}</div>
                                       </div>
                                       <div class="col-auto">
                                           <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                                       </div>
                                   </div>
                               </div>
                           </div>
                       </div>

                       <!-- Caja -->
                                              <div class="col-xl-4 col-md-6 mb-4">
                                                  <div class="card border-left-success shadow h-100 py-2">
                                                      <div class="card-body">
                                                          <div class="row no-gutters align-items-center">
                                                              <div class="col mr-2">
                                                                  <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                                                      Caja
                                                                  </div>
                                                                  <div class="h5 mb-0 font-weight-bold text-gray-800">${caja}</div>
                                                              </div>
                                                              <div class="col-auto">
                                                                  <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                                                              </div>
                                                          </div>
                                                      </div>
                                                  </div>
                                              </div>
                    </div>
                   <!-- Fila 2: Mes -->
                   <div class="row">
                       <!-- Ventas Mes -->
                       <div class="col-xl-6 col-md-6 mb-4">
                           <div class="card border-left-warning shadow h-100 py-2">
                               <div class="card-body">
                                   <div class="row no-gutters align-items-center">
                                       <div class="col mr-2">
                                           <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                               Ventas Mes
                                           </div>
                                           <div class="h5 mb-0 font-weight-bold text-gray-800">${ventasMes}</div>
                                       </div>
                                       <div class="col-auto">
                                           <i class="fas fa-calendar-alt fa-2x text-gray-300"></i>
                                       </div>
                                   </div>
                               </div>
                           </div>
                       </div>

                       <!-- Ganancia Mes -->
                       <div class="col-xl-6 col-md-6 mb-4">
                           <div class="card border-left-success shadow h-100 py-2">
                               <div class="card-body">
                                   <div class="row no-gutters align-items-center">
                                       <div class="col mr-2">
                                           <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                               Ganancia Mes
                                           </div>
                                           <div class="h5 mb-0 font-weight-bold text-gray-800">${gananciaMes}</div>
                                       </div>
                                       <div class="col-auto">
                                           <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                                       </div>
                                   </div>
                               </div>
                           </div>
                       </div>
                   </div>
     </div>

                </div>
     <!-- Footer -->
            <footer class="sticky-footer bg-white">
                <div class="container my-auto">
                    <div class="copyright text-center my-auto">
                        <span>Copyright &copy;</span>
                    </div>
                </div>
            </footer>

    <!-- Bootstrap core JavaScript-->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

    <!-- Core plugin JavaScript-->
    <script src="vendor/jquery-easing/jquery.easing.min.js"></script>

    <!-- Custom scripts for all pages-->
    <script src="js/sb-admin-2.min.js"></script>

    <!-- Page level plugins -->
    <script src="vendor/chart.js/Chart.min.js"></script>

    <!-- Page level custom scripts -->
    <script src="js/demo/chart-area-demo.js"></script>
    <script src="js/demo/chart-pie-demo.js"></script>

</div>

</div>

</body>

</html>