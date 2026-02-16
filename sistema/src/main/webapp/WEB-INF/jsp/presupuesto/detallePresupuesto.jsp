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
            <div class="container-fluid mt-4">

                <!-- MENSAJES -->
                <c:if test="${not empty mensaje}">
                    <div class="alert alert-success">${mensaje}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- NAVEGACIÓN -->
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="mb-0">📄 Detalle de Presupuesto</h4>
                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/presupuestos/nuevo"
                           class="btn btn-primary btn-sm mr-2">
                            + Nuevo Presupuesto
                        </a>
                        <a href="${pageContext.request.contextPath}/presupuestos"
                           class="btn btn-outline-secondary btn-sm">
                            📋 Volver al listado
                        </a>
                    </div>
                </div>

                <!-- CARD PRINCIPAL -->
                <c:if test="${not empty presupuesto}">
                    <div class="card">
                        <!-- HEADER -->
                        <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                            <div>
                                <span class="fs-5 fw-bold">${presupuesto.codigo}</span>
                            </div>
                            <div>
                                <span class="badge fs-6
                                    ${presupuesto.estado == 'PENDIENTE' ? 'bg-warning text-dark' :
                                      presupuesto.estado == 'APROBADO' ? 'bg-success' : 'bg-danger'}">
                                    ${presupuesto.estado}
                                </span>
                            </div>
                        </div>

                        <!-- BODY -->
                        <div class="card-body">
                            <!-- INFO GENERAL -->
                            <div class="row mb-3 pb-3 border-bottom">

                                <div class="col-md-3">
                                    <div class="text-muted">Código</div>
                                    <div class="fw-bold">${presupuesto.codigo}</div>
                                </div>

                                <div class="col-md-3">
                                    <div class="text-muted">Fecha</div>
                                    <div class="small">${fechaPresupuestoFmt}</div>
                                </div>

                                <div class="col-md-3">
                                    <div class="text-muted">Cliente</div>
                                    <div class="small">
                                        <c:choose>
                                            <c:when test="${presupuesto.cliente != null}">
                                                ${presupuesto.cliente.nombre} ${presupuesto.cliente.apellido}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Consumidor Final</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="col-md-3">
                                    <span class="badge
                                        ${presupuesto.estado == 'PENDIENTE' ? 'bg-warning text-dark' :
                                          presupuesto.estado == 'APROBADO' ? 'bg-success' :
                                          'bg-danger'} fs-6">
                                        ${presupuesto.estado}
                                    </span>
                                </div>

                            </div>

                            <!-- TABLA DE ITEMS -->
                            <table class="table table-hover table-striped">
                                <thead class="table-dark">
                                <tr>
                                    <th>Producto</th>
                                    <th class="text-center">Cant.</th>
                                    <th class="text-end">Precio Unit.</th>
                                    <th class="text-end">Subtotal</th>
                                </tr>
                                </thead>

                                <tbody>
                                <c:forEach items="${presupuesto.detalles}" var="d">
                                    <tr>
                                        <td>${d.producto.descripcion}</td>
                                        <td class="text-center">${d.cantidad}</td>
                                        <td class="text-end">
                                            $<fmt:formatNumber value="${d.precioUnitario}" minFractionDigits="2"/>
                                        </td>
                                        <td class="text-end fw-semibold">
                                            $<fmt:formatNumber value="${d.subtotal}" minFractionDigits="2"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>

                                <tfoot class="table-secondary">
                                <c:choose>
                                    <c:when test="${presupuesto.cliente != null
                                                   and presupuesto.cliente.condicionIva == 'RESPONSABLE_INSCRIPTO'}">

                                        <tr>
                                            <td colspan="3" class="text-end fw-bold">Neto</td>
                                            <td class="text-end">
                                                $<fmt:formatNumber value="${totales.totalNeto}" minFractionDigits="2"/>
                                            </td>
                                        </tr>

                                        <c:forEach var="entry" items="${totales.ivasMap}">
                                            <tr>
                                                <td colspan="3" class="text-end fw-bold">
                                                    IVA <c:out value="${entry.key}"/>%
                                                </td>
                                                <td class="text-end">
                                                    $<fmt:formatNumber value="${entry.value}" minFractionDigits="2"/>
                                                </td>
                                            </tr>
                                        </c:forEach>

                                        <tr>
                                            <td colspan="3" class="text-end fw-bold fs-5">TOTAL</td>
                                            <td class="text-end fw-bold fs-5 text-success">
                                                $<fmt:formatNumber value="${totales.total}" minFractionDigits="2"/>
                                            </td>
                                        </tr>

                                    </c:when>

                                    <c:otherwise>
                                        <tr>
                                            <td colspan="3" class="fw-bold">TOTAL</td>
                                            <td class="text-end fw-bold fs-5 text-success">
                                                $<fmt:formatNumber value="${totales.total}" minFractionDigits="2"/>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                                </tfoot>
                            </table>



                            <c:if test="${presupuesto.estado == 'PENDIENTE'}">
                                <div class="d-flex gap-2 mt-3">

                                    <!-- APROBAR -->
                                    <form action="${pageContext.request.contextPath}/presupuestos/aprobar"
                                          method="post"

                                          onsubmit="return confirm('¿Aprobar este presupuesto y generar la venta?')">
                                        <input type="hidden"
                                                                                             name="${_csrf.parameterName}"
                                                                                             value="${_csrf.token}"/>
                                        <input type="hidden" name="id" value="${presupuesto.id}" />

                                        <button type="submit" class="btn btn-success mr-2">
                                            ✅ Aprobar presupuesto
                                        </button>
                                    </form>

                                    <!-- RECHAZAR -->
                                    <form action="${pageContext.request.contextPath}/presupuestos/rechazar"
                                          method="post"
                                          onsubmit="return confirm('¿Rechazar este presupuesto?')">
                                          <input type="hidden"
                                         name="${_csrf.parameterName}"
                                                                                                                                      value="${_csrf.token}"/>
                                        <input type="hidden" name="id" value="${presupuesto.id}" />

                                        <button type="submit" class="btn btn-outline-danger mr-2">
                                            ❌ Rechazar
                                        </button>
                                    </form>

                            </c:if>
                                <!-- Botón Imprimir PDF (para todos los estados) -->
                                <a href="${pageContext.request.contextPath}/presupuestos/${presupuesto.id}/pdf"
                                   class="btn btn-outline-secondary mr-2" target="_blank">
                                    📄 Descargar PDF
                                </a>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Si no hay presupuesto -->
                <c:if test="${empty presupuesto}">
                    <div class="alert alert-warning">
                        No se encontró el presupuesto solicitado.
                    </div>
                </c:if>
            </div>
        </div>


    </div>
</div>

<!-- MODAL APROBAR (seleccionar forma de pago) -->
<div class="modal fade" id="modalAprobar" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title">Aprobar Presupuesto</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/presupuestos/${presupuesto.id}/aprobar"
                  method="post">
                <div class="modal-body">
                    <p>Al aprobar este presupuesto se creará automáticamente una venta
                       y se restará el stock de los productos.</p>

                    <label class="form-label fw-semibold">Forma de pago *</label>
                    <select name="formaPago" class="form-select" required>
                        <option value="">-- Seleccionar --</option>
                        <c:forEach items="${formasPago}" var="fp">
                            <option value="${fp}">${fp}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary"
                            data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-success">
                        ✅ Confirmar Aprobación
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<footer class="sticky-footer bg-white">
            <div class="container my-auto">
                <div class="copyright text-center my-auto">
                    <span>Copyright &copy;</span>
                </div>
            </div>
        </footer>
</body>
</html>