<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

                <c:if test="${not empty mensaje}">
                    <div class="alert alert-success mt-4">${mensaje}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger mt-4">${error}</div>
                </c:if>

                <div class="d-sm-flex align-items-center justify-content-between mb-4 mt-4">
                    <div>
                        <h1 class="h3 mb-1 text-gray-800">Remitos</h1>
                        <p class="mb-0 text-muted">
                            Mercadería entregada pendiente de cobro en cuenta corriente
                        </p>
                    </div>
                    <a href="${pageContext.request.contextPath}/remitos/nuevo"
                       class="btn btn-primary">
                        <i class="fas fa-plus mr-1"></i> Nuevo Remito
                    </a>
                </div>

                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            Listado de Remitos
                        </h6>
                    </div>

                    <div class="d-flex flex-wrap justify-content-between align-items-center m-4">
                        <input type="text" id="searchInput" class="form-control mb-2"
                               style="max-width: 320px"
                               placeholder="Buscar por código o cliente...">

                        <div class="btn-group mb-2">
                            <a href="${pageContext.request.contextPath}/remitos"
                               class="btn btn-sm ${empty estadoFiltro ? 'btn-dark' : 'btn-outline-dark'}">
                                Todos
                            </a>
                            <a href="${pageContext.request.contextPath}/remitos?estado=ENTREGADO"
                               class="btn btn-sm ${estadoFiltro == 'ENTREGADO' ? 'btn-warning' : 'btn-outline-warning'}">
                                Pendientes de cobro
                            </a>
                            <a href="${pageContext.request.contextPath}/remitos?estado=CONVERTIDO"
                               class="btn btn-sm ${estadoFiltro == 'CONVERTIDO' ? 'btn-success' : 'btn-outline-success'}">
                                Pagados
                            </a>
                            <a href="${pageContext.request.contextPath}/remitos?estado=ANULADO"
                               class="btn btn-sm ${estadoFiltro == 'ANULADO' ? 'btn-danger' : 'btn-outline-danger'}">
                                Anulados
                            </a>
                        </div>
                    </div>

                    <div class="card-body pt-0">
                        <div class="table-responsive">
                            <table id="dataTable" class="table table-bordered table-hover">
                                <thead class="thead-dark">
                                <tr>
                                    <th>Código</th>
                                    <th>Cliente</th>
                                    <th>Fecha</th>
                                    <th class="text-right">Total</th>
                                    <th class="text-center">Estado</th>
                                    <th class="text-center">Acciones</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${not empty remitos}">
                                        <c:forEach items="${remitos}" var="r">
                                            <tr>
                                                <td><strong>${r.codigo}</strong></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${r.cliente != null}">
                                                            ${r.cliente.nombre} ${r.cliente.apellido}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted">Consumidor Final</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${r.fechaFormateada}</td>
                                                <td class="text-right">
                                                    <strong>$ <fmt:formatNumber value="${r.total}"
                                                            minFractionDigits="2"
                                                            maxFractionDigits="2"/></strong>
                                                </td>
                                                <td class="text-center">
                                                    <c:choose>
                                                        <c:when test="${r.estado == 'ENTREGADO'}">
                                                            <span class="badge badge-warning">Pendiente de cobro</span>
                                                        </c:when>
                                                        <c:when test="${r.estado == 'CONVERTIDO'}">
                                                            <span class="badge badge-success">Pagado</span>
                                                        </c:when>
                                                        <c:when test="${r.estado == 'ANULADO'}">
                                                            <span class="badge badge-danger">Anulado</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-secondary">${r.estado}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-center">
                                                    <a class="btn btn-sm btn-info"
                                                       href="${pageContext.request.contextPath}/remitos/detalle/${r.id}">
                                                        Ver detalle
                                                    </a>
                                                    <c:if test="${r.estado == 'ENTREGADO'}">
                                                        <a class="btn btn-sm btn-success"
                                                           href="${pageContext.request.contextPath}/remitos/convertir/${r.id}">
                                                            Registrar pago
                                                        </a>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="6" class="text-center text-muted py-4">
                                                No hay remitos registrados
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/foot.jsp"/>
<script>
    document.getElementById('searchInput').addEventListener('keyup', function () {
        const filter = this.value.toLowerCase();
        document.querySelectorAll('#dataTable tbody tr').forEach(function (row) {
            row.style.display = row.textContent.toLowerCase().includes(filter) ? '' : 'none';
        });
    });
</script>
</body>
</html>
