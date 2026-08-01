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
                <div class="row justify-content-center mt-5">
                    <div class="col-lg-7">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h5 class="m-0 font-weight-bold text-primary">
                                    Registrar pago del remito ${remito.codigo}
                                </h5>
                            </div>
                            <div class="card-body">
                                <dl class="row">
                                    <dt class="col-sm-4">Cliente</dt>
                                    <dd class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${remito.cliente != null}">
                                                ${remito.cliente.nombre} ${remito.cliente.apellido}
                                            </c:when>
                                            <c:otherwise>Consumidor Final</c:otherwise>
                                        </c:choose>
                                    </dd>
                                    <dt class="col-sm-4">Fecha de entrega</dt>
                                    <dd class="col-sm-8">${remito.fechaFormateada}</dd>
                                    <dt class="col-sm-4">Total a cobrar</dt>
                                    <dd class="col-sm-8 h5 font-weight-bold text-success">
                                        $ <fmt:formatNumber value="${remito.total}"
                                            minFractionDigits="2" maxFractionDigits="2"/>
                                    </dd>
                                </dl>

                                <div class="alert alert-info">
                                    Al confirmar se creará la venta usando los precios guardados en el remito.
                                    El stock no volverá a descontarse.
                                </div>

                                <c:choose>
                                    <c:when test="${remito.estado == 'ENTREGADO'}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/remitos/convertir/${remito.id}">
                                            <div class="form-group">
                                                <label for="formaPago">Forma en que abonó *</label>
                                                <select id="formaPago" name="formaPago"
                                                        class="form-control" required>
                                                    <option value="">Seleccionar...</option>
                                                    <option value="CONTADO">Efectivo / contado</option>
                                                    <option value="TARJETA">Tarjeta</option>
                                                </select>
                                            </div>
                                            <div class="text-right">
                                                <a href="${pageContext.request.contextPath}/remitos/detalle/${remito.id}"
                                                   class="btn btn-secondary mr-2">Cancelar</a>
                                                <button type="submit" class="btn btn-success">
                                                    Confirmar pago y crear venta
                                                </button>
                                            </div>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert alert-warning mb-0">
                                            Este remito ya no se encuentra pendiente de cobro.
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/foot.jsp"/>
</body>
</html>
