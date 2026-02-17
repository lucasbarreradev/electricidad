<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sistema.model.Producto" %>
<%@ page import="com.sistema.model.Cliente" %>

<%
List<Producto> productos = (List<Producto>) request.getAttribute("productos");
List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
String error = (String) request.getAttribute("error");
%>

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

                <!-- Mensajes -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="row">
                    <!-- COLUMNA IZQUIERDA: Productos -->
                    <div class="col-lg-9 col-md-8 col-sm-12">
                        <div class="card shadow mb-4">
                            <div class="card-header bg-primary text-white">
                                <h6 class="mb-0">📦 Detalles del Presupuesto</h6>
                            </div>
                            <div class="card-body">

                                <!-- BUSCADOR -->
                                <div class="row mb-3">
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">Buscar producto</label>
                                        <input type="text"
                                               id="buscarProducto"
                                               class="form-control"
                                               placeholder="Escribí el nombre del producto...">
                                        <div id="resultados"
                                             class="list-group position-absolute w-100"
                                             style="z-index:1000; max-height: 300px; overflow-y: auto;"></div>
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
                                        <input type="number" id="precio" class="form-control" step="0.01" readonly>
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
                                        <button class="btn btn-success w-100"
                                                onclick="agregarProducto()">
                                            + Agregar
                                        </button>
                                    </div>
                                </div>

                                <!-- TABLA DE ITEMS -->
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover">
                                        <thead class="table-dark">
                                        <tr>
                                            <th>Producto</th>
                                            <th class="text-center">Cant.</th>
                                            <th class="text-end">Precio Unit.</th>
                                            <th class="text-center">Desc. %</th>
                                            <th class="text-end">Subtotal</th>
                                            <th class="text-center" style="width: 60px;"></th>
                                        </tr>
                                        </thead>
                                        <tbody id="detallePresupuesto">
                                        <tr>
                                            <td colspan="6" class="text-center text-muted py-4">
                                                No hay productos agregados. Buscá y agregá productos arriba.
                                            </td>
                                        </tr>
                                        </tbody>
                                        <tfoot class="table-secondary">
                                        <tr>
                                            <td colspan="4" class="fw-bold text-end">TOTAL:</td>
                                            <td class="text-end fw-bold fs-5 text-success">
                                                $<span id="totalDisplay">0.00</span>
                                            </td>
                                            <td></td>
                                        </tr>
                                        </tfoot>
                                    </table>
                                </div>

                            </div>
                        </div>
                    </div>

                    <!-- COLUMNA DERECHA: Datos generales -->
                    <div class="col-lg-3 col-md-4 col-sm-12">
                        <div class="card shadow mb-4 sticky-top" style="top: 20px;">
                            <div class="card-header bg-success text-white">
                                <h6 class="mb-0">📝 Datos Generales</h6>
                            </div>
                            <div class="card-body">

                                <form id="formPresupuesto" method="post"
                                      action="${pageContext.request.contextPath}/presupuestos/guardar"
                                      target="_blank">

                                      <input type="hidden"
                                                 name="${_csrf.parameterName}"
                                                 value="${_csrf.token}"/>

                                    <div class="col-md-12 mb-3 position-relative">
                                                           <label>Cliente</label>
                                                           <input type="text"
                                                                  id="buscarCliente"
                                                                  class="form-control"
                                                                  placeholder="Escribí el nombre del cliente..."
                                                                  autocomplete="off">
                                                           <input type="hidden" name="clienteId" id="clienteId">

                                                           <div id="resultadosCliente"
                                                                class="list-group position-absolute w-100"
                                                                style="z-index:1050; max-height:200px; overflow-y:auto;"></div>
                                                       </div>

                                    <!-- FORMA DE PAGO -->
                                                        <label>Forma de pago</label>
                                                        <select name="formaPago" class="form-control mb-3">
                                                            <option value="CONTADO">Contado</option>
                                                            <option value="TARJETA">Tarjeta</option>
                                                            <option value="CUENTA_CORRIENTE">Cuenta Corriente</option>
                                                        </select>

                                    <hr>

                                    <!-- Resumen -->
                                    <div class="mb-3">
                                        <small class="text-muted">Productos:</small>
                                        <div class="fs-5 fw-bold text-primary">
                                            <span id="cantidadItems">0</span> items
                                        </div>
                                    </div>

                                    <div class="mb-4">
                                        <small class="text-muted">TOTAL A PRESUPUESTAR:</small>
                                        <div class="fs-3 fw-bold text-success">
                                            $<span id="totalGeneral">0.00</span>
                                        </div>
                                    </div>

                                    <!-- Inputs hidden para enviar los items -->
                                    <div id="itemsHidden"></div>

                                    <button type="submit" class="btn btn-success w-100 mb-2">
                                        💾 Guardar Presupuesto
                                    </button>

                                    <a href="${pageContext.request.contextPath}/presupuestos"
                                       class="btn btn-outline-secondary w-100">
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

<script>
console.log('Script cargado');

let items = [];
let productoSeleccionado = null;
let clienteSeleccionado = null;
let productoDescripcion = "";

// ==========================================
// AGREGAR PRODUCTO A LA LISTA
// ==========================================
function agregarProducto() {
    if (!productoSeleccionado) {
        alert("Seleccioná un producto primero");
        return;
    }

    let cantidad = parseInt(document.getElementById("cantidad").value);
    let precio = parseFloat(document.getElementById("precio").value);
    let descuentoPct = parseFloat(document.getElementById("descuento").value || 0);
    let stock = parseInt(document.getElementById("stock").value);

    // Validaciones
    if (!cantidad || cantidad <= 0) {
        alert("La cantidad debe ser mayor a 0");
        return;
    }

    if (cantidad > stock) {
        if (!confirm('La cantidad solicitada (' + cantidad + ') supera el stock disponible (' + stock + '). ¿Continuar igual?')) {
            return;
        }
    }

    // Calcular subtotal
    let subtotalBruto = cantidad * precio;
    let descuentoMonto = subtotalBruto * (descuentoPct / 100);
    let subtotal = subtotalBruto - descuentoMonto;

    // Agregar a la lista
    items.push({
        productoId: productoSeleccionado,
        descripcion: productoDescripcion,
        cantidad: cantidad,
        precio: precio,
        descuento: descuentoPct,
        subtotal: subtotal
    });

    // Limpiar campos
    productoSeleccionado = null;
    productoDescripcion = "";
    document.getElementById("buscarProducto").value = "";
    document.getElementById("stock").value = "";
    document.getElementById("precio").value = "";
    document.getElementById("cantidad").value = "1";
    document.getElementById("descuento").value = "0";

    renderTabla();
}

// ==========================================
// RENDERIZAR TABLA Y TOTALES
// ==========================================
function renderTabla() {
    let tbody = document.getElementById("detallePresupuesto");
    let hidden = document.getElementById("itemsHidden");
    let total = 0;

    tbody.innerHTML = "";
    hidden.innerHTML = "";

    if (items.length === 0) {
        tbody.innerHTML =
            "<tr>" +
                "<td colspan='6' class='text-center text-muted py-4'>" +
                    "No hay productos agregados. Buscá y agregá productos arriba." +
                "</td>" +
            "</tr>";
        return;
    }

    items.forEach((i, index) => {
        total += i.subtotal;

        tbody.innerHTML +=
            "<tr>" +
                "<td>" + i.descripcion + "</td>" +
                "<td class='text-center'>" + i.cantidad + "</td>" +
                "<td class='text-end'>$" + i.precio + "</td>" +
                "<td class='text-center'>" +
                    (i.descuento > 0 ? i.descuento + "%" : "-") +
                "</td>" +
                "<td class='text-end fw-semibold'>$" + i.subtotal + "</td>" +
                "<td class='text-center'>" +
                    "<button type='button' class='btn btn-danger btn-sm' " +
                            "onclick='eliminarItem(" + index + ")'>✕</button>" +
                "</td>" +
            "</tr>";

        hidden.innerHTML +=
            "<input type='hidden' name='productoIds' value='" + i.productoId + "'>" +
            "<input type='hidden' name='cantidades' value='" + i.cantidad + "'>" +
            "<input type='hidden' name='descuentos' value='" + i.descuento + "'>";
    });

    document.getElementById("totalDisplay").textContent = total;
    document.getElementById("totalGeneral").textContent = total;
    document.getElementById("cantidadItems").textContent = items.length;
}


// ==========================================
// ELIMINAR ITEM
// ==========================================
function eliminarItem(index) {
    items.splice(index, 1);
    renderTabla();
}

let precioContado = 0;
let precioTarjeta = 0;
let precioCC = 0;

// ==========================================
// BÚSQUEDA DE PRODUCTOS
// ==========================================
document.getElementById("buscarProducto").addEventListener("keyup", function() {
    let query = this.value.trim();

    if (query.length < 2) {
        document.getElementById("resultados").innerHTML = "";
        return;
    }

    fetch("${pageContext.request.contextPath}/productos/buscar?q=" + encodeURIComponent(query))
        .then(res => res.json())
        .then(data => {
            let html = "";

            if (data.length === 0) {
                html = '<div class="list-group-item text-muted">No se encontraron productos</div>';
            } else {
                data.forEach(p => {
                    let stock = p.cantidad ?? 0;
                    let precioContado = p.precioContado ?? 0;
                    let precioTarjeta = p.precioTarjeta ?? 0;
                    let precioCC = p.precioCuentaCorriente ?? 0;

                    html +=
                        "<a href='#' class='list-group-item list-group-item-action producto-item' " +
                        "data-id='" + p.id + "' " +
                        "data-descripcion='" + p.descripcion + "' " +
                        "data-stock='" + stock + "' " +
                        "data-precio-contado='" + p.precioContado + "' " +
                        "data-precio-tarjeta='" + p.precioTarjeta + "' " +
                        "data-precio-cc='" + p.precioCuentaCorriente + "'>" +
                        p.descripcion + " | Stock: " + stock +
                        "</a>";

                });


            }

            document.getElementById("resultados").innerHTML = html;
        })
        .catch(err => {
            console.error('Error buscando productos:', err);
        });
});

// ==========================================
// SELECCIONAR PRODUCTO DEL DROPDOWN
// ==========================================
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
        item.dataset.precioCc
    );
});

function seleccionarProducto(id, descripcion, stock, pContado, pTarjeta, pCC) {
    productoSeleccionado = Number(id);
    productoDescripcion = descripcion;

    precioContado = parseFloat(pContado);
        precioTarjeta = parseFloat(pTarjeta);
        precioCC = parseFloat(pCC);

    document.getElementById("buscarProducto").value = descripcion;
    document.getElementById("stock").value = Number(stock);
    document.getElementById("cantidad").value = "1";

    actualizarPrecioSegunFormaPago();

    document.getElementById("resultados").innerHTML = "";

    // Fokusear cantidad para agilizar
    document.getElementById("cantidad").focus();
}

// ==========================================
// VALIDACIÓN ANTES DE ENVIAR
// ==========================================
document.getElementById("formPresupuesto").addEventListener("submit", function(e) {
    if (items.length === 0) {
        e.preventDefault();
        alert("Debe agregar al menos un producto al presupuesto");
        return false;
    }

    console.log('Enviando presupuesto con', items.length, 'items');
    return true;
});

// Cerrar resultados al hacer clic fuera
document.addEventListener('click', function(e) {
    if (!e.target.closest('#buscarProducto') && !e.target.closest('#resultados')) {
        document.getElementById('resultados').innerHTML = '';
    }
});

function actualizarPrecioSegunFormaPago() {

    let formaPago = document.querySelector("select[name='formaPago']").value;
    let precio = 0;

    if (formaPago === "CONTADO") {
        precio = precioContado;
    } else if (formaPago === "TARJETA") {
        precio = precioTarjeta;
    } else if (formaPago === "CUENTA_CORRIENTE") {
        precio = precioCC;
    }

    document.getElementById("precio").value = precio.toFixed(2);
}
document.querySelector("select[name='formaPago']")
    .addEventListener("change", function () {
        actualizarPrecioSegunFormaPago();
    });

    document.getElementById("buscarCliente").addEventListener("keyup", function () {
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
                        "data-nombre='" + c.nombre + "' " +
                        "data-apellido='" + c.apellido + "'>" +
                        c.nombre + " " + c.apellido +
                        "</a>";
                });

                document.getElementById("resultadosCliente").innerHTML = html;
            });
    });

    // Event listener para clicks en los resultados
    document.getElementById("resultadosCliente").addEventListener("click", function (e) {
        e.preventDefault();
        let item = e.target.closest(".cliente-item");
        if (!item) return;

        seleccionarCliente(
            item.dataset.id,
            item.dataset.nombre,
            item.dataset.apellido
        );
    });

    function seleccionarCliente(id, nombre, apellido) {
        clienteSeleccionado = id;

        document.getElementById("clienteId").value = id;
        document.getElementById("buscarCliente").value = nombre + " " + apellido;
        document.getElementById("resultadosCliente").innerHTML = "";
    }

</script>

<style>
/* Responsive */
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
        -webkit-overflow-scrolling: touch;
    }
}

/* Resultados búsqueda */
#resultados {
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.producto-item {
    cursor: pointer;
    transition: background-color 0.2s;
}

.producto-item:hover {
    background-color: #f8f9fa !important;
}

/* Sticky sidebar en desktop */
@media (min-width: 992px) {
    .sticky-top {
        position: sticky !important;
    }
}
</style>

</body>
</html>