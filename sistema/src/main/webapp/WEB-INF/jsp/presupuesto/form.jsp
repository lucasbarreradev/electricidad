<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- TÍTULO DINÁMICO -->
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="mb-0">
                        <c:choose>
                            <c:when test="${not empty presupuesto}">
                                ✏️ Editar Presupuesto ${presupuesto.codigo}
                                <span class="badge
                                    <c:choose>
                                        <c:when test="${presupuesto.estado == 'PENDIENTE'}">bg-warning text-dark</c:when>
                                        <c:when test="${presupuesto.estado == 'APROBADO'}">bg-success</c:when>
                                        <c:otherwise>bg-secondary</c:otherwise>
                                    </c:choose>
                                ">
                                    ${presupuesto.estado}
                                </span>
                            </c:when>
                            <c:otherwise>
                                📝 Nuevo Presupuesto
                            </c:otherwise>
                        </c:choose>
                    </h4>
                    <a href="${pageContext.request.contextPath}/presupuestos"
                       class="btn btn-secondary btn-sm">
                        ← Volver
                    </a>
                </div>

                <div class="row">
                    <!-- COLUMNA IZQUIERDA: Productos -->
                    <div class="col-lg-9 col-md-8 col-sm-12">
                        <div class="card shadow mb-4">
                            <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                <span>📦 Detalles del Presupuesto</span>
                                <div>
                                    <button type="button" class="btn btn-light btn-sm mr-2"
                                            data-toggle="modal" data-target="#modalItemManual">
                                        ✍️ Agregar ítem manual
                                    </button>
                                    <small class="badge bg-light text-dark">Paso 2: Agregar ítems</small>
                                </div>
                            </div>
                            <div class="card-body">

                                <!-- BUSCADOR -->
                                <div class="row mb-3">
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">🔍 Buscar producto</label>
                                        <input type="text"
                                               id="buscarProducto"
                                               class="form-control form-control-lg"
                                               placeholder="Escribí el nombre o código del producto..."
                                               autocomplete="off">
                                        <div id="resultados"
                                             class="list-group position-absolute w-100"
                                             style="z-index:1000; max-height: 400px; overflow-y: auto;"></div>
                                    </div>
                                </div>

                                <!-- DATOS DEL PRODUCTO -->
                                <div class="row mb-3">
                                    <div class="col-md-3">
                                        <label class="form-label">Stock disponible</label>
                                        <input type="text" id="stock" class="form-control" readonly>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Cantidad *</label>
                                        <input type="number" id="cantidad" class="form-control" min="1" value="1">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Precio unitario</label>
                                        <input type="text" id="precio" class="form-control" readonly>
                                        <small class="text-muted" id="textoPrecio"></small>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Descuento (%)</label>
                                        <input type="number"
                                               id="descuento"
                                               class="form-control"
                                               value="0"
                                               min="0"
                                               max="100"
                                               step="0.01">
                                    </div>
                                    <div class="col-md-2 d-flex align-items-end">
                                        <button class="btn btn-success w-100" onclick="agregarProducto()">
                                            + Agregar
                                        </button>
                                    </div>
                                </div>

                                <!-- TABLA -->
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover">
                                        <thead class="table-dark">
                                        <tr>
                                            <th>Producto</th>
                                            <th class="text-center">Cant.</th>
                                            <th class="text-end">Precio Unit.</th>
                                            <th class="text-center">Desc. %</th>
                                            <th class="text-center">IVA %</th>
                                            <th class="text-end">Subtotal</th>
                                            <th class="text-center" style="width: 60px;"></th>
                                        </tr>
                                        </thead>
                                        <tbody id="detallePresupuesto">
                                        <tr>
                                            <td colspan="7" class="text-center text-muted py-4">
                                                No hay ítems agregados.
                                            </td>
                                        </tr>
                                        </tbody>

                                        <tfoot class="table-secondary">
                                            <tr>
                                                <td colspan="5" class="fw-bold text-end">SUBTOTAL:</td>
                                                <td class="text-end fw-bold fs-5 text-dark">
                                                    <span id="simboloSubtotal">$</span><span id="subtotalEfectivo">0.00</span>
                                                </td>
                                                <td></td>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>

                            </div>
                        </div>
                    </div>

                    <!-- COLUMNA DERECHA -->
                    <div class="col-lg-3 col-md-4 col-sm-12">
                        <div class="card shadow mb-4">
                            <div class="card-header bg-success text-white">
                                <h6 class="mb-0">📝 Datos del Presupuesto</h6>
                            </div>
                            <div class="card-body">

                                <!-- FORM: acción dinámica según nuevo o edición -->
                                <form id="formPresupuesto"
                                      method="post"
                                      action="<c:choose>
                                                <c:when test='${not empty presupuesto}'>
                                                    ${pageContext.request.contextPath}/presupuestos/${presupuesto.id}/actualizar
                                                </c:when>
                                                <c:otherwise>
                                                    ${pageContext.request.contextPath}/presupuestos/guardar
                                                </c:otherwise>
                                              </c:choose>"
                                      onsubmit="return validarPresupuesto()">

                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                                    <!-- CLIENTE -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            <small class="badge bg-secondary text-white">Paso 1</small>
                                            Cliente (opcional)
                                        </label>
                                        <input type="text"
                                               id="buscarCliente"
                                               class="form-control"
                                               placeholder="Buscar cliente..."
                                               value="${not empty presupuesto.cliente ? presupuesto.cliente.nombre.concat(' ').concat(presupuesto.cliente.apellido) : ''}"
                                               autocomplete="off">
                                        <input type="hidden"
                                               name="clienteId"
                                               id="clienteId"
                                               value="${not empty presupuesto.cliente ? presupuesto.cliente.id : ''}">

                                        <div id="resultadosCliente"
                                             class="list-group position-absolute w-100"
                                             style="z-index:1050; max-height:200px; overflow-y:auto;"></div>
                                        <small class="text-muted">Dejá vacío para "Consumidor Final"</small>
                                    </div>

                                    <hr>

                                    <hr>

                                    <!-- VÁLIDO HASTA -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            <small class="badge bg-info text-dark">Paso 2</small>
                                            📅 Válido hasta
                                        </label>
                                        <input type="date"
                                               name="fechaValidez"
                                               id="fechaValidez"
                                               class="form-control"
                                               value="${not empty presupuesto.fechaValidez ? presupuesto.fechaValidez : ''}">
                                        <small class="text-muted">Dejá vacío para 30 días por defecto</small>
                                    </div>

                                    <hr>

                                    <!-- RESUMEN -->
                                    <div class="mb-3">
                                        <small class="text-muted">Ítems agregados:</small>
                                        <div class="fs-5 fw-bold text-primary">
                                            <span id="cantidadItems">0</span> items
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <small class="text-muted">Subtotal:</small>
                                        <div class="fs-4 fw-bold text-dark">
                                            $<span id="subtotalGeneral">0.00</span>
                                        </div>
                                    </div>

                                    <hr>



                                    <!-- FORMA DE PAGO -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            <small class="badge bg-warning text-dark">Paso 3</small>
                                            💳 Forma de pago *
                                        </label>
                                        <select name="formaPago"
                                                id="formaPago"
                                                class="form-select form-select-lg"
                                                required
                                                onchange="actualizarPreciosFinal()">
                                            <option value="">-- Seleccionar --</option>
                                            <option value="CONTADO"
                                                ${presupuesto.formaPago == 'CONTADO' ? 'selected' : ''}>
                                                💵 Efectivo
                                            </option>
                                            <option value="TARJETA"
                                                ${presupuesto.formaPago == 'TARJETA' ? 'selected' : ''}>
                                                💳 Tarjeta
                                            </option>
                                            <option value="CUENTA_CORRIENTE"
                                                ${presupuesto.formaPago == 'CUENTA_CORRIENTE' ? 'selected' : ''}>
                                                📋 Cuenta Corriente
                                            </option>
                                        </select>
                                    </div>

                                    <hr>

                                    <!-- MONEDA -->
                                                                        <div class="mb-3">
                                                                            <label class="form-label fw-semibold">
                                                                                                                        <small class="badge bg-danger text-white">Paso 4</small>💱 Moneda</label>
                                                                            <div class="btn-group w-100" role="group">
                                                                                <input type="radio" class="btn-check" name="moneda"
                                                                                       id="monedaARS" value="ARS"
                                                                                       ${empty presupuesto || presupuesto.moneda == 'ARS' ? 'checked' : ''}
                                                                                       onchange="cambiarMoneda('ARS')">
                                                                                <label class="btn btn-outline-primary" for="monedaARS">
                                                                                    🇦🇷 Pesos (ARS)
                                                                                </label>

                                                                                <input type="radio" class="btn-check" name="moneda"
                                                                                       id="monedaUSD" value="USD"
                                                                                       ${presupuesto.moneda == 'USD' ? 'checked' : ''}
                                                                                       onchange="cambiarMoneda('USD')">
                                                                                <label class="btn btn-outline-success" for="monedaUSD">
                                                                                    🇺🇸 Dólares (USD)
                                                                                </label>
                                                                            </div>
                                                                        </div>

                                                                        <!-- TIPO DE CAMBIO (solo visible si USD) -->
                                                                        <div class="mb-3" id="tipoCambioSection"
                                                                             style="${presupuesto.moneda == 'USD' ? '' : 'display:none;'}">
                                                                            <label class="form-label fw-semibold">💵 Tipo de cambio</label>
                                                                            <div class="input-group">
                                                                                <span class="input-group-text">$1 USD =</span>
                                                                                <input type="number"
                                                                                       id="tipoCambio"
                                                                                       name="tipoCambio"
                                                                                       class="form-control"
                                                                                       step="0.01"
                                                                                       min="1"
                                                                                       value="${not empty presupuesto.tipoCambio ? presupuesto.tipoCambio : 1}"
                                                                                       oninput="recalcularPreciosUSD()">
                                                                                <span class="input-group-text">ARS</span>
                                                                                </div>
                                                                                <div class="mt-2">
                                                                                                                            <label class="form-label">Nota sobre el tipo de cambio</label>
                                                                                                                            <textarea name="notaTipoCambio"
                                                                                                                                      id="notaTipoCambio"
                                                                                                                                      class="form-control"
                                                                                                                                      rows="2"
                                                                                                                                      maxlength="200">${not empty presupuesto.notaTipoCambio ? presupuesto.notaTipoCambio : ''}</textarea>
                                                                                                                        </div>

                                                                            <small class="text-muted">Los precios se convierten automáticamente</small>
                                                                        </div>

                                                        <hr>

                                    <!-- TOTAL -->
                                    <div class="mb-4 p-3 bg-light rounded">
                                        <small class="text-muted">TOTAL A PRESUPUESTAR:</small>
                                        <div class="fs-2 fw-bold text-success">
                                            <span id="simboloTotal">$</span><span id="totalFinal">0.00</span>
                                        </div>
                                    </div>

                                    <div id="itemsHidden"></div>

                                    <button type="submit"
                                            class="btn btn-success btn-lg w-100 mb-2"
                                            id="btnGuardar"
                                            disabled>
                                        <c:choose>
                                            <c:when test="${not empty presupuesto}">
                                                💾 Guardar Cambios
                                            </c:when>
                                            <c:otherwise>
                                                💾 Guardar Presupuesto
                                            </c:otherwise>
                                        </c:choose>
                                    </button>

                                    <small class="text-muted d-block text-center mt-2" id="mensajeAyuda">
                                                ⬆️ Agregá productos o ítems manuales
                                    </small>

                                    <a href="${pageContext.request.contextPath}/presupuestos"
                                       class="btn btn-outline-secondary w-100 mt-2">
                                        Cancelar
                                    </a>

                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- ÍTEM MANUAL -->
<div class="modal fade" id="modalItemManual" tabindex="-1" role="dialog"
     aria-labelledby="modalItemManualLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="modalItemManualLabel">Agregar ítem manual</h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <form id="formItemManual">
                <div class="modal-body">
                    <div class="form-group">
                        <label>Descripción *</label>
                        <textarea id="manualDescripcion" class="form-control"
                                  rows="5" required></textarea>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label>Cantidad *</label>
                            <input type="number" id="manualCantidad" class="form-control"
                                   min="1" value="1" required>
                        </div>
                        <div class="form-group col-md-6">
                            <label>Precio unitario *</label>
                            <input type="number" id="manualPrecio" class="form-control"
                                   min="0" step="0.01" required>
                        </div>
                        <div class="form-group col-md-6">
                            <label>Descuento (%)</label>
                            <input type="number" id="manualDescuento" class="form-control"
                                   min="0" max="100" step="0.01" value="0">
                        </div>
                        <div class="form-group col-md-6">
                            <label>IVA (%)</label>
                            <input type="number" id="manualIva" class="form-control"
                                   min="0" max="100" step="0.01" value="21">
                        </div>
                    </div>
                    <small class="text-muted">
                        El precio debe ingresarse en la moneda seleccionada para el presupuesto.
                    </small>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Agregar al presupuesto</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/foot.jsp"/>

<!-- Datos seguros para reconstruir los renglones al editar. -->
<div id="itemsExistentesData" class="d-none">
    <c:if test="${not empty presupuesto}">
        <c:forEach items="${presupuesto.detalles}" var="detalle">
            <div class="item-existente"
                 data-producto-id="${detalle.producto != null ? detalle.producto.id : 0}"
                 data-cantidad="${detalle.cantidad}"
                 data-precio-contado="${detalle.producto != null && not empty detalle.producto.precioContado ? detalle.producto.precioContado : detalle.precioUnitario}"
                 data-precio-tarjeta="${detalle.producto != null && not empty detalle.producto.precioTarjeta ? detalle.producto.precioTarjeta : detalle.precioUnitario}"
                 data-precio-cc="${detalle.producto != null && not empty detalle.producto.precioCuentaCorriente ? detalle.producto.precioCuentaCorriente : detalle.precioUnitario}"
                 data-descuento="${not empty detalle.descuentoPct ? detalle.descuentoPct : 0}"
                 data-iva="${not empty detalle.alicuotaIva ? detalle.alicuotaIva : 0}"
                 data-manual="${detalle.producto == null}"
                 data-precio-manual="${not empty detalle.precioUnitario ? detalle.precioUnitario : 0}">
                <textarea class="descripcion-existente"><c:out value="${detalle.descripcionMostrada}"/></textarea>
            </div>
        </c:forEach>
    </c:if>
</div>

<script>
const itemsExistentes = Array.from(
    document.querySelectorAll("#itemsExistentesData .item-existente")
).map(elemento => ({
    productoId: Number(elemento.dataset.productoId),
    descripcion: elemento.querySelector(".descripcion-existente").value,
    cantidad: Number(elemento.dataset.cantidad),
    precioContado: Number(elemento.dataset.precioContado),
    precioTarjeta: Number(elemento.dataset.precioTarjeta),
    precioCC: Number(elemento.dataset.precioCc),
    descuento: Number(elemento.dataset.descuento),
    alicuotaIva: Number(elemento.dataset.iva),
    manual: elemento.dataset.manual === "true",
    precioManual: Number(elemento.dataset.precioManual)
}));

let monedaActual = '${not empty presupuesto.moneda ? presupuesto.moneda : "ARS"}';
let simboloMoneda = monedaActual === 'USD' ? 'U$D ' : '$ ';
let tipoCambio = ${not empty presupuesto.tipoCambio ? presupuesto.tipoCambio : 1};

let items = [];
let productoSeleccionado = null;
let clienteSeleccionado = null;
let productoDescripcion = "";
let precioContado = 0;
let precioTarjeta = 0;
let precioCC = 0;
let alicuotaIvaSeleccionada = 0;

// ==========================================
// PRECARGAR ITEMS AL EDITAR
// ==========================================
if (itemsExistentes && itemsExistentes.length > 0) {
    items = itemsExistentes.map(i => ({
        productoId: i.productoId,
        descripcion: i.descripcion,
        cantidad: i.cantidad,
        precioContado: parseFloat(i.precioContado),
        precioTarjeta: parseFloat(i.precioTarjeta),
        precioCC: parseFloat(i.precioCC),
        descuento: parseFloat(i.descuento),
        alicuotaIva: parseFloat(i.alicuotaIva),
        manual: Boolean(i.manual),
        precioManual: parseFloat(i.precioManual) // ← precio guardado en el detalle
    }));
    renderTabla();
    actualizarPreciosFinal();
}

// ==========================================
// AGREGAR PRODUCTO
// ==========================================
function agregarProducto() {
    if (!productoSeleccionado) {
        alert("⚠️ Seleccioná un producto primero");
        return;
    }

    let cantidad = parseInt(document.getElementById("cantidad").value);
    let stock = parseInt(document.getElementById("stock").value);
    let descuentoPct = parseFloat(document.getElementById("descuento").value || 0);

    if (!cantidad || cantidad <= 0) {
        alert("⚠️ La cantidad debe ser mayor a 0");
        return;
    }

    if (cantidad > stock) {
        if (!confirm(`⚠️ La cantidad (${cantidad}) supera el stock (${stock}). ¿Continuar?`)) {
            return;
        }
    }

    let itemExistente = items.find(i => i.productoId === productoSeleccionado);

    if (itemExistente) {
        itemExistente.cantidad += cantidad;
    } else {
        items.push({
            productoId: productoSeleccionado,
            descripcion: productoDescripcion,
            cantidad: cantidad,
            precioContado: precioContado,
            precioTarjeta: precioTarjeta,
            precioCC: precioCC,
            descuento: descuentoPct,
            alicuotaIva: alicuotaIvaSeleccionada,
            manual: false,
            actualizarProducto: false
        });
    }

    limpiarSeleccion();
    renderTabla();
    actualizarPreciosFinal();
}

function limpiarSeleccion() {
    productoSeleccionado = null;
    productoDescripcion = "";
    document.getElementById("buscarProducto").value = "";
    document.getElementById("stock").value = "";
    document.getElementById("precio").value = "";
    document.getElementById("cantidad").value = "1";
    document.getElementById("descuento").value = "0";
    document.getElementById("textoPrecio").textContent = "";
    alicuotaIvaSeleccionada = 0;
    document.getElementById("buscarProducto").focus();
}

// ==========================================
// RENDERIZAR TABLA
// ==========================================
function renderTabla() {
    let tbody = document.getElementById("detallePresupuesto");
    let hidden = document.getElementById("itemsHidden");

    tbody.innerHTML = "";
    hidden.innerHTML = "";

    if (items.length === 0) {
        tbody.innerHTML =
            "<tr><td colspan='7' class='text-center text-muted py-4'>" +
            "No hay ítems. Buscá un producto o agregá un ítem manual.</td></tr>";
        document.getElementById("btnGuardar").disabled = true;
        document.getElementById("mensajeAyuda").textContent = "⬆️ Agregá productos o ítems manuales";
        document.getElementById("cantidadItems").textContent = "0";
        return;
    }

    items.forEach((item, index) => {
        let formaPago = document.getElementById("formaPago").value;

        let precioBase = item.precioManual != null ? item.precioManual
                   : formaPago === "TARJETA" ? item.precioTarjeta
                   : formaPago === "CUENTA_CORRIENTE" ? item.precioCC
                   : item.precioContado;

        let precioMostrar = monedaActual === 'USD'
            ? parseFloat((precioBase / tipoCambio).toFixed(2))
            : precioBase;

        let subtotalMostrar = item.cantidad * precioMostrar * (1 - item.descuento / 100);

        tbody.innerHTML +=
            "<tr>" +
                "<td style='min-width:240px'>" +
                    "<textarea class='form-control form-control-sm' rows='3' " +
                        "onchange='setDescripcion(" + index + ", this.value)'>" +
                        escaparHtml(item.descripcion) + "</textarea>" +
                    (item.manual ? "<small class='text-primary'>Ítem manual</small>" : "") +
                "</td>" +
                "<td class='text-center'>" +
                    "<div class='input-group input-group-sm' style='width: 100px; margin: auto;'>" +
                        "<button type='button' class='btn btn-outline-secondary btn-sm' onclick='cambiarCantidad(" + index + ", -1)'>-</button>" +
                        "<input type='number' class='form-control text-center' value='" + item.cantidad + "' " +
                            "min='1' onchange='setCantidad(" + index + ", this.value)' style='width: 45px;'>" +
                        "<button type='button' class='btn btn-outline-secondary btn-sm' onclick='cambiarCantidad(" + index + ", 1)'>+</button>" +
                    "</div>" +
                "</td>" +
                "<td class='text-end' style='width: 220px;'>" +
                    "<div class='input-group input-group-sm'>" +
                        "<span class='input-group-text'>" + simboloMoneda + "</span>" +
                        "<input type='number' class='form-control text-end' " +
                               "value='" + precioMostrar.toFixed(2) + "' " +
                               "min='0' step='0.01' " +
                               "onchange='setPrecio(" + index + ", this.value)'>" +
                    "</div>" +
                    (item.precioEditado && item.productoId > 0 ?
                        "<div class='form-check mt-1'>" +
                            "<input class='form-check-input' type='checkbox' " +
                                   (item.actualizarProducto ? "checked" : "") + " " +
                                   "id='chk" + index + "' " +
                                   "onchange='toggleActualizarProducto(" + index + ", this.checked)'>" +
                            "<label class='form-check-label small' for='chk" + index + "'>" +
                                "Actualizar producto" +
                            "</label>" +
                        "</div>"
                        : ""
                    ) +
                "</td>" +
                "<td class='text-center' style='width:100px'>" +
                    "<input type='number' class='form-control form-control-sm text-center' " +
                        "value='" + item.descuento + "' min='0' max='100' step='0.01' " +
                        "onchange='setDescuento(" + index + ", this.value)'>" +
                "</td>" +
                "<td class='text-center' style='width:100px'>" +
                    "<input type='number' class='form-control form-control-sm text-center' " +
                        "value='" + item.alicuotaIva + "' min='0' max='100' step='0.01' " +
                        "onchange='setIva(" + index + ", this.value)'>" +
                "</td>" +
                "<td class='text-end fw-semibold'>" + simboloMoneda + subtotalMostrar.toFixed(2) + "</td>" +
                "<td class='text-center'><button type='button' class='btn btn-danger btn-sm' onclick='eliminarItem(" + index + ")'>✕</button></td>" +
            "</tr>";

        hidden.innerHTML +=
            "<input type='hidden' name='productoIds' value='" + item.productoId + "'>" +
            "<input type='hidden' name='descripciones' value='" + escaparHtml(item.descripcion) + "'>" +
            "<input type='hidden' name='cantidades' value='" + item.cantidad + "'>" +
            "<input type='hidden' name='descuentos' value='" + item.descuento + "'>" +
            "<input type='hidden' name='alicuotasIva' value='" + item.alicuotaIva + "'>" +
            "<input type='hidden' name='precios' value='" + precioBase.toFixed(2) + "'>";

        if (item.precioEditado && item.actualizarProducto) {
            hidden.innerHTML +=
                "<input type='hidden' name='actualizarPrecioProducto' value='" +
                item.productoId + "'>";
        }
    });

    document.getElementById("cantidadItems").textContent = items.length;
    verificarHabilitarBoton();
}

function escaparHtml(valor) {
    return String(valor == null ? '' : valor)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

// ==========================================
// CAMBIAR CANTIDAD DIRECTAMENTE EN TABLA
// ==========================================
function cambiarCantidad(index, delta) {
    let nuevaCantidad = items[index].cantidad + delta;
    if (nuevaCantidad < 1) return;
    items[index].cantidad = nuevaCantidad;
    renderTabla();
    actualizarPreciosFinal();
}

function setCantidad(index, valor) {
    let nuevaCantidad = parseInt(valor);
    if (isNaN(nuevaCantidad) || nuevaCantidad < 1) return;
    items[index].cantidad = nuevaCantidad;
    renderTabla();
    actualizarPreciosFinal();
}

function setDescripcion(index, valor) {
    let descripcion = String(valor || '').trim();
    if (!descripcion) {
        alert("⚠️ La descripción no puede quedar vacía");
        renderTabla();
        return;
    }
    items[index].descripcion = descripcion;
    renderTabla();
}

function setDescuento(index, valor) {
    let descuento = parseFloat(valor);
    if (isNaN(descuento) || descuento < 0 || descuento > 100) {
        alert("⚠️ El descuento debe estar entre 0 y 100");
        renderTabla();
        return;
    }
    items[index].descuento = descuento;
    renderTabla();
    actualizarPreciosFinal();
}

function setIva(index, valor) {
    let iva = parseFloat(valor);
    if (isNaN(iva) || iva < 0 || iva > 100) {
        alert("⚠️ El IVA debe estar entre 0 y 100");
        renderTabla();
        return;
    }
    items[index].alicuotaIva = iva;
    renderTabla();
}

function setPrecio(index, valor) {

    let nuevoPrecio = parseFloat(valor);

    if (isNaN(nuevoPrecio) || nuevoPrecio < 0) {
        return;
    }

    if (monedaActual === 'USD') {
        items[index].precioManual =
            parseFloat((nuevoPrecio * tipoCambio).toFixed(2));
    } else {
        items[index].precioManual = nuevoPrecio;
    }

    items[index].precioEditado = true;
    items[index].actualizarProducto = items[index].productoId > 0;

    renderTabla();
    actualizarPreciosFinal();
}

// ==========================================
// ELIMINAR ITEM
// ==========================================
function eliminarItem(index) {
    if (confirm("¿Eliminar este ítem del presupuesto?")) {
        items.splice(index, 1);
        renderTabla();
        actualizarPreciosFinal();
    }
}

// ==========================================
// ACTUALIZAR PRECIOS
// ==========================================
function actualizarPreciosFinal() {
    const formaPago = document.getElementById("formaPago").value;
    let totalARS = 0;

    items.forEach(item => {
        let precio = item.precioManual != null ? item.precioManual
                   : formaPago === "TARJETA" ? item.precioTarjeta
                   : formaPago === "CUENTA_CORRIENTE" ? item.precioCC
                   : item.precioContado;
        totalARS += item.cantidad * precio * (1 - item.descuento / 100);
    });

    // Convertir a la moneda seleccionada para mostrar
    let totalMostrar = monedaActual === 'USD'
        ? parseFloat((totalARS / tipoCambio).toFixed(2))
        : totalARS;

    document.getElementById("simboloSubtotal").textContent = simboloMoneda;
    document.getElementById("simboloTotal").textContent = simboloMoneda;
    document.getElementById("subtotalEfectivo").textContent = totalMostrar.toFixed(2);
    document.getElementById("subtotalGeneral").textContent = totalMostrar.toFixed(2);
    document.getElementById("totalFinal").textContent = totalMostrar.toFixed(2);

    let detalle = "";
    if (formaPago === "TARJETA") detalle = "Precio con tarjeta";
    else if (formaPago === "CUENTA_CORRIENTE") detalle = "Precio con cuenta corriente";
    else if (formaPago === "CONTADO") detalle = "Precio en efectivo";


    renderTabla();
    verificarHabilitarBoton();
}

function toggleActualizarProducto(index, checked) {
    items[index].actualizarProducto = checked;
}

// ==========================================
// VERIFICAR BOTÓN
// ==========================================
function verificarHabilitarBoton() {
    const hayProductos = items.length > 0;
    const hayFormaPago = document.getElementById("formaPago").value !== "";

    if (hayProductos && hayFormaPago) {
        document.getElementById("btnGuardar").disabled = false;
        document.getElementById("mensajeAyuda").textContent = "✅ Todo listo para guardar";
        document.getElementById("mensajeAyuda").className = "text-success d-block text-center mt-2 fw-bold";
    } else if (hayProductos && !hayFormaPago) {
        document.getElementById("btnGuardar").disabled = true;
        document.getElementById("mensajeAyuda").textContent = "💳 Seleccioná la forma de pago";
        document.getElementById("mensajeAyuda").className = "text-warning d-block text-center mt-2";
    } else {
        document.getElementById("btnGuardar").disabled = true;
        document.getElementById("mensajeAyuda").textContent = "⬆️ Agregá productos o ítems manuales";
        document.getElementById("mensajeAyuda").className = "text-muted d-block text-center mt-2";
    }
}

function validarPresupuesto() {
    if (items.length === 0) {
        alert("⚠️ Agregá al menos un ítem");
        return false;
    }
    if (!document.getElementById("formaPago").value) {
        alert("⚠️ Seleccioná la forma de pago");
        return false;
    }
    document.getElementById("btnGuardar").disabled = true;
    document.getElementById("btnGuardar").textContent = "Guardando...";
    return true;
}

// ==========================================
// BÚSQUEDA DE PRODUCTOS
// ==========================================
document.getElementById("buscarProducto").addEventListener("keyup", function() {
    let q = this.value;
    if (q.length < 2) {
        document.getElementById("resultados").innerHTML = "";
        return;
    }

    fetch("${pageContext.request.contextPath}/productos/buscar?q=" + encodeURIComponent(q))
        .then(res => res.json())
        .then(data => {
            let html = "";
            if (data.length === 0) {
                html = '<div class="list-group-item text-muted">No se encontraron productos</div>';
            } else {
                data.forEach(p => {
                    let stock = p.cantidad || 0;
                    let badgeClass = stock <= 5 ? 'bg-danger text-white'
                                   : stock <= 20 ? 'bg-warning text-dark'
                                   : 'bg-success text-white';

                    html +=
                        "<a href='#' class='list-group-item list-group-item-action producto-item' " +
                        "data-id='" + p.id + "' " +
                        "data-descripcion='" + (p.descripcion || '') + "' " +
                        "data-stock='" + stock + "' " +
                        "data-precio-contado='" + (p.precioContado || 0) + "' " +
                        "data-precio-tarjeta='" + (p.precioTarjeta || 0) + "' " +
                        "data-precio-cc='" + (p.precioCuentaCorriente || 0) + "' " +
                        "data-iva='" + ivaDesdeTipo(p.tipoIva) + "'>" +
                        "<strong>" + (p.descripcion || 'Sin nombre') + "</strong>" +
                        "<br><small class='text-muted'>" +
                            "Efectivo: $" + (p.precioContado || 0) +
                            " | Stock: <span class='badge " + badgeClass + "'>" + stock + "</span>" +
                        "</small>" +
                        "</a>";
                });
            }
            document.getElementById("resultados").innerHTML = html;
        });
});

document.getElementById("resultados").addEventListener("click", function(e) {
    e.preventDefault();
    let item = e.target.closest(".producto-item");
    if (!item) return;

    seleccionarProducto(
        item.dataset.id,
        item.dataset.descripcion,
        item.dataset.stock,
        item.dataset.precioContado,
        item.dataset.precioTarjeta,
        item.dataset.precioCc,
        item.dataset.iva
    );
});

function ivaDesdeTipo(tipoIva) {
    switch (tipoIva) {
        case "IVA_21": return 21;
        case "IVA_10_5": return 10.5;
        case "IVA_27": return 27;
        default: return 0;
    }
}

function seleccionarProducto(id, descripcion, stock, pContado, pTarjeta, pCC, iva) {
    productoSeleccionado = Number(id);
    productoDescripcion = descripcion;
    precioContado = parseFloat(pContado);
    precioTarjeta = parseFloat(pTarjeta);
    precioCC = parseFloat(pCC || 0);
    alicuotaIvaSeleccionada = parseFloat(iva || 0);

    document.getElementById("buscarProducto").value = descripcion;
    document.getElementById("stock").value = Number(stock);
    document.getElementById("cantidad").value = "1";
    document.getElementById("precio").value = pContado;
    document.getElementById("textoPrecio").textContent =
        "Tarjeta: $" + pTarjeta + " | C/C: $" + pCC;

    document.getElementById("resultados").innerHTML = "";
    document.getElementById("cantidad").focus();
}

document.getElementById("formItemManual").addEventListener("submit", function(e) {
    e.preventDefault();

    const descripcion = document.getElementById("manualDescripcion").value.trim();
    const cantidad = parseInt(document.getElementById("manualCantidad").value, 10);
    const precioIngresado = parseFloat(document.getElementById("manualPrecio").value);
    const descuento = parseFloat(document.getElementById("manualDescuento").value || 0);
    const iva = parseFloat(document.getElementById("manualIva").value || 0);

    if (!descripcion || !Number.isInteger(cantidad) || cantidad <= 0 ||
            isNaN(precioIngresado) || precioIngresado < 0 ||
            isNaN(descuento) || descuento < 0 || descuento > 100 ||
            isNaN(iva) || iva < 0 || iva > 100) {
        alert("Completá una descripción y valores válidos para el ítem manual.");
        return;
    }

    const precioBase = monedaActual === "USD"
        ? precioIngresado * tipoCambio
        : precioIngresado;

    items.push({
        productoId: 0,
        descripcion: descripcion,
        cantidad: cantidad,
        precioContado: precioBase,
        precioTarjeta: precioBase,
        precioCC: precioBase,
        precioManual: precioBase,
        precioEditado: true,
        actualizarProducto: false,
        descuento: descuento,
        alicuotaIva: iva,
        manual: true
    });

    this.reset();
    document.getElementById("manualCantidad").value = "1";
    document.getElementById("manualDescuento").value = "0";
    document.getElementById("manualIva").value = "21";
    $("#modalItemManual").modal("hide");
    renderTabla();
    actualizarPreciosFinal();
});

// ==========================================
// BÚSQUEDA DE CLIENTES
// ==========================================
document.getElementById("buscarCliente").addEventListener("keyup", function() {
    let q = this.value;
    if (q.length < 2) {
        document.getElementById("resultadosCliente").innerHTML = "";
        return;
    }

    fetch("${pageContext.request.contextPath}/clientes/buscar?q=" + encodeURIComponent(q))
        .then(res => res.json())
        .then(data => {
            let html = "";
            data.forEach(c => {
                html +=
                    "<a href='#' class='list-group-item list-group-item-action cliente-item' " +
                    "data-id='" + c.id + "' " +
                    "data-nombre='" + (c.nombre || '') + "' " +
                    "data-apellido='" + (c.apellido || '') + "'>" +
                    (c.nombre || '') + " " + (c.apellido || '') +
                    "</a>";
            });
            document.getElementById("resultadosCliente").innerHTML = html;
        });
});

document.getElementById("resultadosCliente").addEventListener("click", function(e) {
    e.preventDefault();
    let item = e.target.closest(".cliente-item");
    if (!item) return;
    seleccionarCliente(item.dataset.id, item.dataset.nombre, item.dataset.apellido);
});

function seleccionarCliente(id, nombre, apellido) {
    clienteSeleccionado = id;
    document.getElementById("clienteId").value = id;
    document.getElementById("buscarCliente").value = nombre + " " + apellido;
    document.getElementById("resultadosCliente").innerHTML = "";
}

document.getElementById("formaPago").addEventListener("change", actualizarPreciosFinal);

document.addEventListener('click', function(e) {
    if (!e.target.closest('#buscarProducto') && !e.target.closest('#resultados')) {
        document.getElementById('resultados').innerHTML = '';
    }
    if (!e.target.closest('#buscarCliente') && !e.target.closest('#resultadosCliente')) {
        document.getElementById('resultadosCliente').innerHTML = '';
    }
});

function cambiarMoneda(moneda) {
    monedaActual = moneda;
    simboloMoneda = moneda === 'USD' ? 'U$D ' : '$ ';

    document.getElementById('tipoCambioSection').style.display =
        moneda === 'USD' ? 'block' : 'none';

    recalcularPreciosUSD();
    renderTabla();
    actualizarPreciosFinal();
}

function recalcularPreciosUSD() {
    tipoCambio = parseFloat(document.getElementById('tipoCambio').value) || 1;

    renderTabla();
    actualizarPreciosFinal();
}

</script>

<style>
@media (max-width: 1000px) {
    .col-lg-9, .col-lg-3 {
        flex: 0 0 100%;
        max-width: 100%;
        margin-top: 1rem;
    }
    table.table {
        display: block;
        overflow-x: auto;
        white-space: nowrap;
    }
}
#resultados, #resultadosCliente {
    box-shadow: 0 4px 8px rgba(0,0,0,0.15);
    border-radius: 4px;
}
.producto-item:hover, .cliente-item:hover {
    background-color: #f0f9ff !important;
    cursor: pointer;
}
</style>

</body>
</html>
