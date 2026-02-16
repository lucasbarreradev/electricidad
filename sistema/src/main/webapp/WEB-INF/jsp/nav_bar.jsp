<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

        <!-- Sidebar -->
        <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

            <!-- Sidebar - Brand -->
            <a class="sidebar-brand d-flex align-items-center justify-content-center" href="<c:url value='/' />">
                <div class="sidebar-brand-icon rotate-n-15">
                    <i class="fa-solid fa-plug"></i>
                </div>
                <div class="sidebar-brand-text mx-3">Sistema Stock</div>
            </a>

            <!-- Divider -->
            <hr class="sidebar-divider my-0">

              <!-- Nav Item - Menu -->
                                                <li class="nav-item">
                                                    <a class="nav-link" href="<c:url value='/' />">
                                                        <i class="fa-solid fa-bars"></i>
                                                        <span>Menú Principal</span>
                                                    </a>
                                                </li>

            <!-- Divider -->
            <hr class="sidebar-divider">

            <!-- Nav Item - Presupuestos -->
                                    <li class="nav-item">
                                        <a class="nav-link" href="<c:url value='/presupuestos' />">
                                            <i class="fa-solid fa-address-book"></i>
                                            <span>Presupuestos</span>
                                        </a>
                                    </li>

            <!-- Nav Item - Ventas -->
                                    <li class="nav-item">
                                        <a class="nav-link" href="<c:url value='/ventas' />">
                                            <i class="fa-solid fa-cart-arrow-down"></i>
                                            <span>Ventas</span>
                                        </a>
                                    </li>

            <!-- Nav Item - Productos -->
                                    <li class="nav-item">
                                        <a class="nav-link" href="<c:url value='/productos' />">
                                            <i class="fa-solid fa-bolt"></i>
                                            <span>Productos</span>
                                        </a>
                                    </li>

            <!-- Nav Item - Proveedores -->
            <li class="nav-item">
                <a class="nav-link" href="<c:url value='/proveedores' />">
                    <i class="fa-solid fa-building-user"></i>
                    <span>Proveedores</span>
                </a>
            </li>

            <!-- Nav Item - Clientes -->
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value='/clientes' />">
                                <i class="fa-solid fa-user-group"></i>
                                <span>Clientes</span>
                            </a>
                        </li>

            <!-- Divider -->
            <hr class="sidebar-divider d-none d-md-block">

            <!-- Sidebar Toggler (Sidebar) -->
            <div class="text-center d-none d-md-inline">
                <button class="rounded-circle border-0" id="sidebarToggle"></button>
            </div>


        </ul>

        <!-- End of Sidebar -->
