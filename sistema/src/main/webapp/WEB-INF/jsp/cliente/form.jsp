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
                        <c:when test="${cliente.id != null}">
                            Editar Cliente
                        </c:when>
                        <c:otherwise>
                            Nuevo Cliente
                        </c:otherwise>
                    </c:choose>
                </h1>

                <!-- Card -->
                <div class="card shadow mb-4">
                    <div class="card-body">

                        <!-- Acción del form -->
                        <c:choose>
                            <c:when test="${cliente.id != null}">
                                <c:url var="formAction" value="/clientes/actualizar/${cliente.id}"/>
                            </c:when>
                            <c:otherwise>
                                <c:url var="formAction" value="/clientes/guardar"/>
                            </c:otherwise>
                        </c:choose>

                        <form method="post" action="${formAction}">
                        <input type="hidden"
                                   name="${_csrf.parameterName}"
                                   value="${_csrf.token}"/>
                            <div class="row">

                                <div class="col-md-6 mb-3">
                                    <label>Nombre</label>
                                    <input type="text" name="nombre"
                                           class="form-control"
                                           value="${cliente.nombre}" required>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label>Apellido</label>
                                    <input type="text" name="apellido"
                                           class="form-control"
                                           value="${cliente.apellido}">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label>Teléfono</label>
                                    <input type="text" name="telefono"
                                           class="form-control"
                                           value="${cliente.telefono}">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label>Dni</label>
                                    <input type="text" name="dni"
                                           class="form-control"
                                           value="${cliente.dni}">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label>Email</label>
                                    <input type="email" name="email"
                                           class="form-control"
                                           value="${cliente.email}">
                                </div>

                                <div class="col-md-6 mb-3">
                                                                    <label>Dirección</label>
                                                                    <input type="text" name="direccion"
                                                                           class="form-control"
                                                                           value="${cliente.direccion}">
                                                                </div>



                            <div class="col-md-6 mb-3">
                                <label>Condición IVA</label>
                                <select name="condicionIva" class="form-control" required>
                                    <c:forEach var="cond" items="${condicionesIva}">
                                        <option value="${cond}"
                                            <c:if test="${cliente.condicionIva == cond}">
                                                selected
                                            </c:if>>
                                            ${cond.descripcion}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            </div>

                            <!-- Botones -->
                            <div class="mt-4">
                                <button type="submit" class="btn btn-success"
                                        onclick="this.disabled=true; this.form.submit();">
                                    Guardar
                                </button>

                                <a href="<c:url value='/clientes'/>"
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

