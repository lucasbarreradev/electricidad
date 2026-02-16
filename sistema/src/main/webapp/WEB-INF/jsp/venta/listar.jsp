<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/jsp/head.jsp"/>
</head>

<body id="page-top">

<div id="wrapper">
    <jsp:include page="/WEB-INF/jsp/nav_bar.jsp"/>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">
            <div class="container-fluid">

                <!-- Header -->
                                <div class="d-sm-flex align-items-center justify-content-between mb-4 mt-4">
                                    <h1 class="h3 mb-0 text-gray-800">Ventas</h1>

                                    <a href="${pageContext.request.contextPath}/ventas/nueva"
                                       class="btn btn-primary">
                                        + Nueva Venta
                                    </a>
                                </div>

                                <!-- Card -->
                                <div class="card shadow mb-4">

                                    <div class="card-header py-3">
                                        <h6 class="m-0 font-weight-bold text-primary">
                                            Listado de Ventas
                                        </h6>
                                    </div>
                                    <div class="d-sm-flex align-items-center m-4">
                                        <div class="ms-auto" style="max-width: 300px;">
                                            <input type="text" id="searchInput"
                                                   class="form-control"
                                                   placeholder="Buscar venta...">
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

                <div class="card shadow mb-4">
                    <div class="card-body">

                        <table class="table table-bordered table-hover" id="dataTable">
                            <thead class="thead-dark">
                            <tr>
                                <th>ID</th>
                                <th>Código</th>
                                <th>Cliente</th>
                                <th>Forma Pago</th>
                                <th>Fecha</th>
                                <th>Acciones</th>
                            </tr>
                            </thead>
                            <tbody>

                            <c:forEach items="${ventas}" var="v">
                                <tr>
                                    <td>${v.id}</td>
                                    <td>${v.codigo}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${v.cliente != null}">
                                                ${v.cliente.nombre} ${v.cliente.apellido}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Consumidor Final</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>${v.formaPago}</td>
                                    <td>${v.fechaFormateada}</td>
                                    <td class="text-center">
                                        <a class="btn btn-sm btn-info"
                                           href="${pageContext.request.contextPath}/ventas/detalle/${v.id}">
                                            Ver
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>

                            </tbody>
                        </table>

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
    </div>
</div>


