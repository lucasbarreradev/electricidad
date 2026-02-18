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

                <!-- Título -->
                <h1 class="h3 mb-4 text-gray-800 mt-4">
                    <c:choose>
                        <c:when test="${producto.id != null}">
                            Editar Producto
                        </c:when>
                        <c:otherwise>
                            Nuevo Producto
                        </c:otherwise>
                    </c:choose>
                </h1>

                <!-- Card -->
                <div class="card shadow mb-4">
                    <div class="card-body">

                        <!-- Acción del form -->
                        <c:choose>
                            <c:when test="${producto.id != null}">
                                <c:url var="formAction" value="/productos/actualizar/${producto.id}"/>
                            </c:when>
                            <c:otherwise>
                                <c:url var="formAction" value="/productos/guardar"/>
                            </c:otherwise>
                        </c:choose>

                        <form method="post" action="${formAction}">
                        <input type="hidden"
                                   name="${_csrf.parameterName}"
                                   value="${_csrf.token}"/>

                            <div class="row">

                            <div class="col-md-6 mb-3">
                                                                <label>Proveedor</label>

                                                                <div class="input-group">
                                                                    <input type="text"
                                                                           class="form-control"
                                                                           value="${producto.proveedor.nombreRazonSocial}"
                                                                           readonly>

                                                                    <input type="hidden" name="proveedorId"
                                                                           value="${producto.proveedor.id}">

                                                                    <a href="${pageContext.request.contextPath}/proveedores?origen=producto&productoId=${producto.id}"
                                                                       class="btn btn-primary ml-3">
                                                                        Buscar Proveedor
                                                                    </a>
                                                                </div>
                                                            </div>

                                <div class="col-md-6 mb-3">
                                    <label>Descripción</label>
                                    <input type="text" name="descripcion"
                                           class="form-control"
                                           value="${producto.descripcion}" required>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label>Cantidad</label>
                                    <input type="text" name="cantidad"
                                           class="form-control"
                                           value="${producto.cantidad}">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label>Precio Compra</label>
                                    <input type="text"
                                           name="precioCompra"
                                           class="form-control"
                                           value="${producto.precioCompra}"
                                           oninput="this.value = this.value.replace(',', '.')"
                                           pattern="^\d+(\.\d{0,2})?$"
                                           title="Use solo números y puntos">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label>Precio Contado</label>
                                    <input type="text"
                                           name="precioContado"
                                           class="form-control"
                                           value="${producto.precioContado}"
                                           oninput="this.value = this.value.replace(',', '.')"
                                           pattern="^\d+(\.\d{0,2})?$"
                                           title="Use solo números y puntos">
                                </div>
                                <div class="col-md-6 mb-3">
                                                                    <label>Precio Cuenta Corriente</label>
                                                                    <input type="text"
                                                                           name="precioCuentaCorriente"
                                                                           class="form-control"
                                                                           value="${producto.precioCuentaCorriente}"
                                                                           oninput="this.value = this.value.replace(',', '.')"
                                                                           pattern="^\d+(\.\d{0,2})?$"
                                                                           title="Use solo números y puntos">
                                                                </div>
                            <div class="col-md-6 mb-3">
                                                                <label>Precio Tarjeta</label>
                                                                <input type="text"
                                                                       name="precioTarjeta"
                                                                       class="form-control"
                                                                       value="${producto.precioTarjeta}"
                                                                       oninput="this.value = this.value.replace(',', '.')"
                                                                       pattern="^\d+(\.\d{0,2})?$"
                                                                       title="Use solo números y puntos">
                                                            </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Tipo IVA</label>

                                <select name="tipoIva" class="form-select" required>

                                    <c:forEach items="${tiposIva}" var="iva" varStatus="status">
                                        <option value="${iva}"
                                            <c:if test="${producto.tipoIva == iva or status.first}">
                                                selected
                                            </c:if>
                                        >
                                            ${iva.descripcion}
                                        </option>
                                    </c:forEach>

                                </select>
                            </div>
                            <!-- Botones -->
                            <div class="mt-4">
                                <button type="submit" class="btn btn-success">
                                    Guardar
                                </button>

                                <a href="<c:url value='/productos'/>"
                                   class="btn btn-secondary">
                                    Cancelar
                                </a>
                            </div>

                        </form>

                    </div>
                </div>

            </div>
        </div>

        <!-- Footer -->
        <footer class="sticky-footer bg-white">
            <div class="container my-auto">
                <div class="copyright text-center my-auto">
                    <span>© Sistema Stock</span>
                </div>
            </div>
        </footer>

    </div>
</div>

</body>
</html>