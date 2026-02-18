<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <jsp:include page="/WEB-INF/jsp/head.jsp"/>
</head>

<body id="page-top">

<div id="wrapper">

    <jsp:include page="/WEB-INF/jsp/nav_bar.jsp"/>

    <div id="content-wrapper" class="d-flex flex-column">

        <div id="content">

            <div class="container-fluid">

                <!-- Mensajes -->
                <c:if test="${not empty mensaje}">
                    <div class="alert alert-success">${mensaje}</div>
                </c:if>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- Header -->
                <div class="d-sm-flex align-items-center justify-content-between mb-4 mt-4">
                    <h1 class="h3 mb-0 text-gray-800">Proveedores</h1>

                    <a href="${pageContext.request.contextPath}/proveedores/nuevo"
                       class="btn btn-primary">
                        + Nuevo Proveedor
                    </a>
                </div>

                <!-- Card -->
                <div class="card shadow mb-4">

                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            Listado de Proveedores
                        </h6>
                    </div>
                    <div class="d-sm-flex align-items-center m-4">
                        <div class="ms-auto" style="max-width: 300px;">
                            <input type="text" id="searchInput"
                                   class="form-control"
                                   placeholder="Buscar proveedor...">
                        </div>
                    </div>

                    <script>
                    document.getElementById('searchInput').addEventListener('keyup', function () {
                        const filter = this.value.toLowerCase();
                        const rows = document.querySelectorAll('#dataTable tbody tr');

                        rows.forEach(row => {
                            row.style.display = row.textContent.toLowerCase().includes(filter)
                                ? ''
                                : 'none';
                        });
                    });
                    </script>

                    <div class="card-body">
                        <div class="table-responsive">

                            <table class="table table-bordered table-hover"
                                   id="dataTable"
                                   width="100%" cellspacing="0">

                                <thead class="table-dark">
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre/Razón Social</th>
                                    <th>Teléfono</th>
                                    <th>Email</th>
                                    <th>Cuit</th>
                                    <th>Condición Iva</th>
                                    <th>Dirección</th>
                                    <th>Acciones</th>
                                </tr>
                                </thead>

                                <tbody>
                                <c:forEach items="${proveedores}" var="p">
                                    <tr>
                                        <td>${p.id}</td>
                                        <td>${p.nombreRazonSocial}</td>
                                        <td>${p.telefono}</td>
                                        <td>${p.email}</td>
                                        <td>${p.cuit}</td>
                                        <td>${p.condicionIva}</td>
                                        <td>${p.direccion}</td>
                                        <td class="text-center">
                                            <c:when test="${origen == 'producto'}">

                                                <c:choose>

                                                    <c:when test="${not empty productoId}">
                                                        <c:url var="urlSeleccionar"
                                                               value="/productos/editar/${productoId}">
                                                            <c:param name="proveedorId" value="${p.id}" />
                                                        </c:url>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <c:url var="urlSeleccionar"
                                                               value="/productos/nuevo">
                                                            <c:param name="proveedorId" value="${p.id}" />
                                                        </c:url>
                                                    </c:otherwise>

                                                </c:choose>

                                                <a class="btn btn-sm btn-success"
                                                   href="${urlSeleccionar}">
                                                    Seleccionar
                                                </a>

                                            </c:when>



                                            <c:otherwise>
                                            <a class="btn btn-sm btn-warning"
                                               href="${pageContext.request.contextPath}/proveedores/editar/${p.id}">
                                                Editar
                                            </a>
                                            <form method="post"
                                                                                              action="${pageContext.request.contextPath}/proveedores/eliminar/${p.id}"
                                                                                              style="display:inline;"
                                                                                              onsubmit="return confirm('¿Eliminar proveedor?');">

                                                                                            <input type="hidden"
                                                                                                   name="${_csrf.parameterName}"
                                                                                                   value="${_csrf.token}" />

                                                                                            <button type="submit" class="btn btn-sm btn-danger">
                                                                                                Eliminar
                                                                                            </button>
                                                                                        </form>
                                            </c:otherwise>

                                             </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>

                            </table>

                        </div>
                    </div>
                </div>

            </div> <!-- container-fluid -->
        </div> <!-- content -->

        <!-- Footer -->
        <footer class="sticky-footer bg-white">
            <div class="container my-auto">
                <div class="copyright text-center my-auto">
                    <span>Copyright &copy;</span>
                </div>
            </div>
        </footer>

    </div> <!-- content-wrapper -->
</div> <!-- wrapper -->
