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
                        <h1 class="h3 mb-1 text-gray-800">Remito ${remito.codigo}</h1>
                        <span class="text-muted">Emitido el ${remito.fechaFormateada}</span>
                    </div>
                    <div>
                        <a href="${pageContext.request.contextPath}/remitos"
                           class="btn btn-outline-secondary mr-2">Volver</a>
                        <a href="${pageContext.request.contextPath}/remitos/${remito.id}/pdf"
                           class="btn btn-info">
                            <i class="fas fa-file-pdf mr-1"></i> Descargar PDF
                        </a>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-8">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Productos entregados</h6>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-bordered mb-0">
                                        <thead class="thead-light">
                                        <tr>
                                            <th>SKU</th>
                                            <th>Descripción</th>
                                            <th class="text-center">Cantidad</th>
                                            <th class="text-right">Precio unitario</th>
                                            <th class="text-center">Desc.</th>
                                            <th class="text-right">Subtotal</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${remito.items}" var="item">
                                            <tr>
                                                <td>${item.producto.sku}</td>
                                                <td>${item.descripcionMostrada}</td>
                                                <td class="text-center">${item.cantidad}</td>
                                                <td class="text-right">
                                                    $ <fmt:formatNumber value="${item.precioUnitario}"
                                                        minFractionDigits="2" maxFractionDigits="2"/>
                                                </td>
                                                <td class="text-center">
                                                    <fmt:formatNumber value="${item.descuentoPct}"
                                                        minFractionDigits="0" maxFractionDigits="2"/>%
                                                </td>
                                                <td class="text-right">
                                                    $ <fmt:formatNumber value="${item.subtotal}"
                                                        minFractionDigits="2" maxFractionDigits="2"/>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                        <tfoot>
                                        <tr>
                                            <th colspan="5" class="text-right">Total pendiente</th>
                                            <th class="text-right">
                                                $ <fmt:formatNumber value="${remito.total}"
                                                    minFractionDigits="2" maxFractionDigits="2"/>
                                            </th>
                                        </tr>
                                        </tfoot>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Datos del remito</h6>
                            </div>
                            <div class="card-body">
                                <p><strong>Cliente:</strong><br>
                                    <c:choose>
                                        <c:when test="${remito.cliente != null}">
                                            ${remito.cliente.nombre} ${remito.cliente.apellido}
                                        </c:when>
                                        <c:otherwise>Consumidor Final</c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Precios calculados por:</strong><br>
                                    <c:choose>
                                        <c:when test="${remito.formaPagoPrecio == 'CONTADO'}">Efectivo / contado</c:when>
                                        <c:when test="${remito.formaPagoPrecio == 'TARJETA'}">Tarjeta</c:when>
                                        <c:when test="${remito.formaPagoPrecio == 'CUENTA_CORRIENTE'}">Cuenta corriente</c:when>
                                        <c:otherwise>No especificado</c:otherwise>
                                    </c:choose>
                                </p>
                                <c:if test="${not empty remito.direccionEntrega}">
                                    <p><strong>Entrega:</strong><br>${remito.direccionEntrega}</p>
                                </c:if>
                                <c:if test="${not empty remito.observaciones}">
                                    <p><strong>Observaciones:</strong><br>${remito.observaciones}</p>
                                </c:if>
                                <p><strong>Estado:</strong><br>
                                    <c:choose>
                                        <c:when test="${remito.estado == 'ENTREGADO'}">
                                            <span class="badge badge-warning">Pendiente de cobro</span>
                                        </c:when>
                                        <c:when test="${remito.estado == 'CONVERTIDO'}">
                                            <span class="badge badge-success">Pagado</span>
                                        </c:when>
                                        <c:when test="${remito.estado == 'ANULADO'}">
                                            <span class="badge badge-danger">Anulado</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-secondary">${remito.estado}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <p class="mb-0">
                                    <strong>Inventario:</strong><br>
                                    <c:choose>
                                        <c:when test="${remito.stockDescontado}">
                                            <span class="text-success">
                                                <i class="fas fa-check-circle"></i> Stock descontado
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Sin salida de stock</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>

                        <c:if test="${remito.estado == 'ENTREGADO'}">
                            <a href="${pageContext.request.contextPath}/remitos/convertir/${remito.id}"
                               class="btn btn-success btn-block mb-2">
                                <i class="fas fa-cash-register mr-1"></i>
                                Registrar pago y crear venta
                            </a>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/remitos/anular/${remito.id}"
                                  onsubmit="return confirm('¿Anular el remito y devolver el stock?');">
                                <button type="submit" class="btn btn-outline-danger btn-block">
                                    Anular y devolver stock
                                </button>
                            </form>
                        </c:if>

                        <c:if test="${remito.estado == 'CONVERTIDO' && remito.venta != null}">
                            <a href="${pageContext.request.contextPath}/ventas/detalle/${remito.venta.id}"
                               class="btn btn-success btn-block">
                                Ver venta generada
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/foot.jsp"/>
</body>
</html>
