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
            <div class="container-fluid mt-4">

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="mb-0">Nuevo Remito</h4>
                    <a href="${pageContext.request.contextPath}/remitos"
                       class="btn btn-secondary btn-sm">← Volver</a>
                </div>

                <form id="formRemito" method="post"
                      action="${pageContext.request.contextPath}/remitos/guardar"
                      onsubmit="return validarRemito()">

                    <div class="row">
                        <!-- Misma disposición utilizada en Presupuestos -->
                        <div class="col-lg-9 col-md-8 col-sm-12">
                            <div class="card shadow mb-4">
                                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                    <span><i class="fas fa-boxes mr-1"></i> Detalle del Remito</span>
                                    <small class="badge badge-light text-dark">Paso 2: Agregar productos</small>
                                </div>
                                <div class="card-body">

                                    <div class="row mb-3">
                                        <div class="col-md-12 position-relative">
                                            <label class="font-weight-bold">Buscar producto</label>
                                            <input type="text" id="buscarProducto"
                                                   class="form-control form-control-lg"
                                                   placeholder="Escribí el nombre o código del producto..."
                                                   autocomplete="off">
                                            <div id="resultadosProducto"
                                                 class="list-group position-absolute w-100"
                                                 style="z-index:1000; max-height:400px; overflow-y:auto;"></div>
                                        </div>
                                    </div>

                                    <div class="row mb-3">
                                        <div class="col-md-3">
                                            <label>Stock disponible</label>
                                            <input type="text" id="stock" class="form-control" readonly>
                                        </div>
                                        <div class="col-md-2">
                                            <label>Cantidad *</label>
                                            <input type="number" id="cantidad" class="form-control"
                                                   min="1" value="1">
                                        </div>
                                        <div class="col-md-3">
                                            <label id="labelPrecio">Precio según forma de pago</label>
                                            <input type="text" id="precio" class="form-control" readonly>
                                            <small class="text-muted" id="textoPrecio">
                                                Seleccioná la forma de pago
                                            </small>
                                        </div>
                                        <div class="col-md-2">
                                            <label>Descuento (%)</label>
                                            <input type="number" id="descuento" class="form-control"
                                                   value="0" min="0" max="100" step="0.01">
                                        </div>
                                        <div class="col-md-2 d-flex align-items-end">
                                            <button type="button" class="btn btn-success btn-block"
                                                    onclick="agregarProducto()">
                                                + Agregar
                                            </button>
                                        </div>
                                    </div>

                                    <div class="table-responsive">
                                        <table class="table table-bordered table-hover">
                                            <thead class="thead-dark">
                                            <tr>
                                                <th>Producto</th>
                                                <th class="text-center">Cant.</th>
                                                <th class="text-right">Precio Unit.</th>
                                                <th class="text-center">Desc. %</th>
                                                <th class="text-right">Subtotal</th>
                                                <th class="text-center" style="width:60px"></th>
                                            </tr>
                                            </thead>
                                            <tbody id="detalleRemito">
                                            <tr>
                                                <td colspan="6" class="text-center text-muted py-4">
                                                    No hay productos agregados.
                                                </td>
                                            </tr>
                                            </tbody>
                                            <tfoot class="table-secondary">
                                            <tr>
                                                <td colspan="4" class="font-weight-bold text-right">TOTAL:</td>
                                                <td class="text-right font-weight-bold text-dark">
                                                    $<span id="totalRemito">0.00</span>
                                                </td>
                                                <td></td>
                                            </tr>
                                            </tfoot>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-3 col-md-4 col-sm-12">
                            <div class="card shadow mb-4">
                                <div class="card-header bg-success text-white">
                                    <h6 class="mb-0">Datos del Remito</h6>
                                </div>
                                <div class="card-body">
                                    <div class="form-group position-relative">
                                        <div class="d-flex justify-content-between align-items-center mb-1">
                                            <label class="font-weight-bold mb-0">
                                                <small class="badge badge-secondary">Paso 1</small>
                                                Cliente (opcional)
                                            </label>
                                            <button type="button" class="btn btn-sm btn-outline-success"
                                                    data-toggle="modal" data-target="#modalNuevoCliente">
                                                <i class="fas fa-user-plus"></i> Nuevo
                                            </button>
                                        </div>
                                        <input type="text" id="buscarCliente" class="form-control"
                                               placeholder="Buscar cliente..." autocomplete="off">
                                        <input type="hidden" name="clienteId" id="clienteId">
                                        <div id="resultadosCliente"
                                             class="list-group position-absolute w-100"
                                             style="z-index:1050; max-height:220px; overflow-y:auto;"></div>
                                        <small class="text-muted">
                                            Si queda vacío se utilizará Consumidor Final.
                                        </small>
                                    </div>

                                    <hr>

                                    <div class="form-group">
                                        <label class="font-weight-bold">
                                            <small class="badge badge-warning">Paso 3</small>
                                            Forma de pago para los precios *
                                        </label>
                                        <select name="formaPagoPrecio" id="formaPagoPrecio"
                                                class="form-control" required
                                                onchange="actualizarFormaPago()">
                                            <option value="">Seleccionar...</option>
                                            <option value="CONTADO">Efectivo / contado</option>
                                            <option value="TARJETA">Tarjeta</option>
                                            <option value="CUENTA_CORRIENTE">Cuenta corriente</option>
                                        </select>
                                    </div>

                                    <hr>

                                    <div class="form-group">
                                        <label class="font-weight-bold">Dirección de entrega</label>
                                        <input type="text" id="direccionEntrega"
                                               name="direccionEntrega" class="form-control" maxlength="255">
                                    </div>

                                    <div class="form-group">
                                        <label class="font-weight-bold">Observaciones</label>
                                        <textarea name="observaciones" class="form-control"
                                                  rows="3" maxlength="500"></textarea>
                                    </div>

                                    <hr>

                                    <div class="mb-3">
                                        <small class="text-muted">Productos agregados:</small>
                                        <div class="h5 font-weight-bold text-primary mb-0">
                                            <span id="cantidadItems">0</span> items
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <small class="text-muted">Total pendiente:</small>
                                        <div class="h4 font-weight-bold text-dark mb-0">
                                            $<span id="totalResumen">0.00</span>
                                        </div>
                                    </div>

                                    <div class="alert alert-warning small">
                                        El stock se descontará al guardar porque la mercadería se entrega ahora.
                                    </div>

                                    <div id="itemsHidden"></div>

                                    <button type="submit" id="btnGuardar"
                                            class="btn btn-success btn-block" disabled>
                                        Crear remito y descontar stock
                                    </button>
                                    <small id="mensajeAyuda"
                                           class="text-muted d-block text-center mt-2">
                                        Agregá productos y seleccioná la forma de pago
                                    </small>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Alta rápida: está fuera del formulario principal para evitar formularios anidados -->
<div class="modal fade" id="modalNuevoCliente" tabindex="-1" role="dialog"
     aria-labelledby="modalNuevoClienteLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="modalNuevoClienteLabel">
                    <i class="fas fa-user-plus mr-1"></i> Nuevo Cliente
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal" aria-label="Cerrar">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form id="formClienteRapido">
                <div class="modal-body">
                    <div id="errorClienteRapido" class="alert alert-danger d-none"></div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label>Nombre *</label>
                            <input type="text" name="nombre" class="form-control" required maxlength="100">
                        </div>
                        <div class="form-group col-md-6">
                            <label>Apellido</label>
                            <input type="text" name="apellido" class="form-control" maxlength="100">
                        </div>
                        <div class="form-group col-md-6">
                            <label>Teléfono</label>
                            <input type="text" name="telefono" class="form-control" maxlength="100">
                        </div>
                        <div class="form-group col-md-6">
                            <label>DNI / CUIT</label>
                            <input type="text" name="dni" class="form-control" maxlength="50">
                        </div>
                        <div class="form-group col-md-6">
                            <label>Email</label>
                            <input type="email" name="email" class="form-control" maxlength="150">
                        </div>
                        <div class="form-group col-md-6">
                            <label>Dirección</label>
                            <input type="text" name="direccion" class="form-control" maxlength="255">
                        </div>
                        <div class="form-group col-md-6">
                            <label>Condición IVA</label>
                            <select name="condicionIva" class="form-control" required>
                                <option value="CONSUMIDOR_FINAL">Consumidor Final</option>
                                <option value="RESPONSABLE_INSCRIPTO">Responsable Inscripto</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                    <button type="submit" id="btnGuardarCliente" class="btn btn-success">
                        Guardar y seleccionar cliente
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/foot.jsp"/>
<script>
let items = [];
let productoSeleccionado = null;

const buscarProducto = document.getElementById('buscarProducto');
const resultadosProducto = document.getElementById('resultadosProducto');
const buscarCliente = document.getElementById('buscarCliente');
const resultadosCliente = document.getElementById('resultadosCliente');

function crearResultadoProducto(producto) {
    const stock = Number(producto.cantidad || 0);
    const link = document.createElement('a');
    link.href = '#';
    link.className = 'list-group-item list-group-item-action producto-item';
    link.dataset.id = producto.id;
    link.dataset.descripcion = producto.descripcion || '';
    link.dataset.stock = stock;
    link.dataset.precioContado = producto.precioContado || 0;
    link.dataset.precioTarjeta = producto.precioTarjeta || 0;
    link.dataset.precioCc = producto.precioCuentaCorriente || 0;

    const titulo = document.createElement('strong');
    titulo.textContent = producto.descripcion || 'Sin nombre';
    link.appendChild(titulo);
    link.appendChild(document.createElement('br'));

    const detalle = document.createElement('small');
    detalle.className = 'text-muted';
    detalle.appendChild(document.createTextNode(
        'Efectivo: $' + Number(producto.precioContado || 0).toFixed(2)
        + ' | Stock: '));

    const badge = document.createElement('span');
    badge.className = 'badge ' + (stock <= 5
        ? 'badge-danger' : stock <= 20 ? 'badge-warning' : 'badge-success');
    badge.textContent = stock;
    detalle.appendChild(badge);
    link.appendChild(detalle);
    return link;
}

buscarProducto.addEventListener('keyup', function () {
    const q = this.value.trim();
    if (q.length < 2) {
        resultadosProducto.innerHTML = '';
        return;
    }

    fetch('${pageContext.request.contextPath}/productos/buscar?q=' + encodeURIComponent(q))
        .then(function (response) {
            if (!response.ok) throw new Error('No se pudo buscar productos');
            return response.json();
        })
        .then(function (productos) {
            resultadosProducto.innerHTML = '';
            if (productos.length === 0) {
                const vacio = document.createElement('div');
                vacio.className = 'list-group-item text-muted';
                vacio.textContent = 'No se encontraron productos';
                resultadosProducto.appendChild(vacio);
                return;
            }
            productos.forEach(function (producto) {
                resultadosProducto.appendChild(crearResultadoProducto(producto));
            });
        })
        .catch(function () {
            resultadosProducto.innerHTML =
                '<div class="list-group-item text-danger">Error al buscar productos</div>';
        });
});

resultadosProducto.addEventListener('click', function (event) {
    event.preventDefault();
    const link = event.target.closest('.producto-item');
    if (!link) return;

    productoSeleccionado = {
        id: Number(link.dataset.id),
        descripcion: link.dataset.descripcion,
        stock: Number(link.dataset.stock),
        precioContado: Number(link.dataset.precioContado),
        precioTarjeta: Number(link.dataset.precioTarjeta),
        precioCC: Number(link.dataset.precioCc)
    };

    buscarProducto.value = productoSeleccionado.descripcion;
    document.getElementById('stock').value = productoSeleccionado.stock;
    document.getElementById('precio').value =
        precioSeleccionado().toFixed(2);
    document.getElementById('cantidad').value = '1';
    document.getElementById('descuento').value = '0';
    resultadosProducto.innerHTML = '';
    document.getElementById('cantidad').focus();
});

function agregarProducto() {
    if (!productoSeleccionado) {
        alert('Seleccioná un producto primero.');
        return;
    }

    const cantidad = Number(document.getElementById('cantidad').value);
    const descuento = Number(document.getElementById('descuento').value || 0);
    if (!Number.isInteger(cantidad) || cantidad <= 0) {
        alert('La cantidad debe ser mayor a cero.');
        return;
    }
    if (descuento < 0 || descuento > 100) {
        alert('El descuento debe estar entre 0 y 100.');
        return;
    }

    const existente = items.find(function (item) {
        return item.productoId === productoSeleccionado.id;
    });
    const cantidadFinal = cantidad + (existente ? existente.cantidad : 0);
    if (cantidadFinal > productoSeleccionado.stock) {
        alert('Stock insuficiente. Disponible: ' + productoSeleccionado.stock);
        return;
    }

    if (existente) {
        existente.cantidad = cantidadFinal;
        existente.descuento = descuento;
    } else {
        items.push({
            productoId: productoSeleccionado.id,
            descripcion: productoSeleccionado.descripcion,
            stock: productoSeleccionado.stock,
            cantidad: cantidad,
            precioContado: productoSeleccionado.precioContado,
            precioTarjeta: productoSeleccionado.precioTarjeta,
            precioCC: productoSeleccionado.precioCC,
            precioManual: null,
            descuento: descuento
        });
    }

    limpiarProducto();
    renderTabla();
}

function limpiarProducto() {
    productoSeleccionado = null;
    buscarProducto.value = '';
    document.getElementById('stock').value = '';
    document.getElementById('precio').value = '';
    document.getElementById('cantidad').value = '1';
    document.getElementById('descuento').value = '0';
    buscarProducto.focus();
}

function renderTabla() {
    const tbody = document.getElementById('detalleRemito');
    const hidden = document.getElementById('itemsHidden');
    tbody.innerHTML = '';
    hidden.innerHTML = '';

    if (items.length === 0) {
        const row = tbody.insertRow();
        const cell = row.insertCell();
        cell.colSpan = 6;
        cell.className = 'text-center text-muted py-4';
        cell.textContent = 'No hay productos. Buscá y agregá productos arriba.';
    }

    let total = 0;
    items.forEach(function (item, index) {
        const precio = precioItem(item);
        const subtotal = item.cantidad * precio * (1 - item.descuento / 100);
        total += subtotal;

        const row = tbody.insertRow();
        const productoCell = row.insertCell();
        const nombre = document.createElement('strong');
        nombre.textContent = item.descripcion;
        productoCell.appendChild(nombre);

        const cantidadCell = row.insertCell();
        cantidadCell.className = 'text-center';
        const grupo = document.createElement('div');
        grupo.className = 'input-group input-group-sm mx-auto';
        grupo.style.width = '110px';

        const menos = document.createElement('button');
        menos.type = 'button';
        menos.className = 'btn btn-outline-secondary btn-sm';
        menos.textContent = '-';
        menos.addEventListener('click', function () { cambiarCantidad(index, -1); });
        grupo.appendChild(menos);

        const cantidadInput = document.createElement('input');
        cantidadInput.type = 'number';
        cantidadInput.className = 'form-control text-center';
        cantidadInput.min = '1';
        cantidadInput.max = item.stock;
        cantidadInput.value = item.cantidad;
        cantidadInput.addEventListener('change', function () {
            setCantidad(index, this.value);
        });
        grupo.appendChild(cantidadInput);

        const mas = document.createElement('button');
        mas.type = 'button';
        mas.className = 'btn btn-outline-secondary btn-sm';
        mas.textContent = '+';
        mas.addEventListener('click', function () { cambiarCantidad(index, 1); });
        grupo.appendChild(mas);
        cantidadCell.appendChild(grupo);

        const precioCell = row.insertCell();
        precioCell.className = 'text-right';
        const precioInput = document.createElement('input');
        precioInput.type = 'number';
        precioInput.className = 'form-control form-control-sm text-right';
        precioInput.min = '0';
        precioInput.step = '0.01';
        precioInput.value = precio.toFixed(2);
        precioInput.addEventListener('change', function () {
            setPrecio(index, this.value);
        });
        precioCell.appendChild(precioInput);

        const descuentoCell = row.insertCell();
        descuentoCell.className = 'text-center';
        descuentoCell.textContent = item.descuento > 0 ? item.descuento + '%' : '-';

        const subtotalCell = row.insertCell();
        subtotalCell.className = 'text-right font-weight-bold';
        subtotalCell.textContent = '$' + subtotal.toFixed(2);

        const accionCell = row.insertCell();
        accionCell.className = 'text-center';
        const eliminar = document.createElement('button');
        eliminar.type = 'button';
        eliminar.className = 'btn btn-danger btn-sm';
        eliminar.textContent = '×';
        eliminar.addEventListener('click', function () { eliminarItem(index); });
        accionCell.appendChild(eliminar);

        agregarHidden(hidden, 'productoIds', item.productoId);
        agregarHidden(hidden, 'cantidades', item.cantidad);
        agregarHidden(hidden, 'precios', precio.toFixed(2));
        agregarHidden(hidden, 'descuentos', item.descuento.toFixed(2));
    });

    document.getElementById('cantidadItems').textContent = items.length;
    document.getElementById('totalRemito').textContent = total.toFixed(2);
    document.getElementById('totalResumen').textContent = total.toFixed(2);
    verificarBoton();
}

function agregarHidden(contenedor, nombre, valor) {
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = nombre;
    input.value = valor;
    contenedor.appendChild(input);
}

function cambiarCantidad(index, delta) {
    const nueva = items[index].cantidad + delta;
    if (nueva < 1 || nueva > items[index].stock) return;
    items[index].cantidad = nueva;
    renderTabla();
}

function setCantidad(index, valor) {
    const nueva = Number(valor);
    if (!Number.isInteger(nueva) || nueva < 1 || nueva > items[index].stock) {
        alert('La cantidad debe estar entre 1 y ' + items[index].stock + '.');
        renderTabla();
        return;
    }
    items[index].cantidad = nueva;
    renderTabla();
}

function setPrecio(index, valor) {
    const precio = Number(valor);
    if (!Number.isFinite(precio) || precio < 0) {
        alert('Ingresá un precio válido.');
        renderTabla();
        return;
    }
    items[index].precioManual = precio;
    renderTabla();
}

function formaPagoSeleccionada() {
    return document.getElementById('formaPagoPrecio').value;
}

function precioSegunForma(precioContado, precioTarjeta, precioCC) {
    const formaPago = formaPagoSeleccionada();
    if (formaPago === 'TARJETA') return precioTarjeta;
    if (formaPago === 'CUENTA_CORRIENTE') return precioCC;
    return precioContado;
}

function precioSeleccionado() {
    if (!productoSeleccionado) return 0;
    return precioSegunForma(
        productoSeleccionado.precioContado,
        productoSeleccionado.precioTarjeta,
        productoSeleccionado.precioCC);
}

function precioItem(item) {
    if (item.precioManual !== null) return item.precioManual;
    return precioSegunForma(
        item.precioContado, item.precioTarjeta, item.precioCC);
}

function actualizarFormaPago() {
    const formaPago = formaPagoSeleccionada();
    const etiquetas = {
        CONTADO: 'Precio en efectivo',
        TARJETA: 'Precio con tarjeta',
        CUENTA_CORRIENTE: 'Precio de cuenta corriente'
    };
    document.getElementById('labelPrecio').textContent =
        etiquetas[formaPago] || 'Precio según forma de pago';
    document.getElementById('textoPrecio').textContent =
        etiquetas[formaPago] || 'Seleccioná la forma de pago';

    if (productoSeleccionado) {
        document.getElementById('precio').value =
            precioSeleccionado().toFixed(2);
    }
    renderTabla();
}

function eliminarItem(index) {
    if (confirm('¿Eliminar este producto del remito?')) {
        items.splice(index, 1);
        renderTabla();
    }
}

function crearResultadoCliente(cliente) {
    const link = document.createElement('a');
    link.href = '#';
    link.className = 'list-group-item list-group-item-action cliente-item';
    link.dataset.id = cliente.id;
    link.dataset.nombre = cliente.nombre || '';
    link.dataset.apellido = cliente.apellido || '';
    link.dataset.direccion = cliente.direccion || '';
    link.textContent = (cliente.nombre || '') + ' ' + (cliente.apellido || '');
    if (cliente.dni) {
        const dni = document.createElement('small');
        dni.className = 'text-muted d-block';
        dni.textContent = 'DNI/CUIT: ' + cliente.dni;
        link.appendChild(dni);
    }
    return link;
}

buscarCliente.addEventListener('keyup', function () {
    document.getElementById('clienteId').value = '';
    verificarBoton();
    const q = this.value.trim();
    if (q.length < 2) {
        resultadosCliente.innerHTML = '';
        return;
    }

    fetch('${pageContext.request.contextPath}/clientes/buscar?q=' + encodeURIComponent(q))
        .then(function (response) {
            if (!response.ok) throw new Error('No se pudo buscar clientes');
            return response.json();
        })
        .then(function (clientes) {
            resultadosCliente.innerHTML = '';
            if (clientes.length === 0) {
                const vacio = document.createElement('div');
                vacio.className = 'list-group-item text-muted';
                vacio.textContent = 'No se encontraron clientes';
                resultadosCliente.appendChild(vacio);
                return;
            }
            clientes.forEach(function (cliente) {
                resultadosCliente.appendChild(crearResultadoCliente(cliente));
            });
        });
});

resultadosCliente.addEventListener('click', function (event) {
    event.preventDefault();
    const link = event.target.closest('.cliente-item');
    if (!link) return;
    seleccionarCliente(
        link.dataset.id,
        link.dataset.nombre,
        link.dataset.apellido,
        link.dataset.direccion);
});

function seleccionarCliente(id, nombre, apellido, direccion) {
    document.getElementById('clienteId').value = id;
    buscarCliente.value = (nombre + ' ' + (apellido || '')).trim();
    resultadosCliente.innerHTML = '';
    const direccionInput = document.getElementById('direccionEntrega');
    if (!direccionInput.value && direccion) direccionInput.value = direccion;
    verificarBoton();
}

document.getElementById('formClienteRapido').addEventListener('submit', function (event) {
    event.preventDefault();
    const form = this;
    const boton = document.getElementById('btnGuardarCliente');
    const error = document.getElementById('errorClienteRapido');
    boton.disabled = true;
    boton.textContent = 'Guardando...';
    error.classList.add('d-none');

    fetch('${pageContext.request.contextPath}/clientes/guardar-rapido', {
        method: 'POST',
        body: new URLSearchParams(new FormData(form))
    })
        .then(function (response) {
            if (!response.ok) throw new Error('No se pudo guardar el cliente');
            return response.json();
        })
        .then(function (cliente) {
            seleccionarCliente(
                cliente.id, cliente.nombre, cliente.apellido, cliente.direccion);
            form.reset();
            $('#modalNuevoCliente').modal('hide');
        })
        .catch(function (err) {
            error.textContent = err.message;
            error.classList.remove('d-none');
        })
        .finally(function () {
            boton.disabled = false;
            boton.textContent = 'Guardar y seleccionar cliente';
        });
});

function verificarBoton() {
    const hayProductos = items.length > 0;
    const hayFormaPago = formaPagoSeleccionada() !== '';
    const listo = hayProductos && hayFormaPago;
    const boton = document.getElementById('btnGuardar');
    const ayuda = document.getElementById('mensajeAyuda');
    boton.disabled = !listo;
    if (listo) {
        ayuda.textContent = 'Todo listo para guardar';
        ayuda.className = 'text-success d-block text-center mt-2 font-weight-bold';
    } else if (!hayProductos) {
        ayuda.textContent = 'Agregá productos para continuar';
        ayuda.className = 'text-muted d-block text-center mt-2';
    } else {
        ayuda.textContent = 'Seleccioná la forma de pago para los precios';
        ayuda.className = 'text-warning d-block text-center mt-2';
    }
}

function validarRemito() {
    if (items.length === 0) {
        alert('Agregá al menos un producto.');
        return false;
    }
    if (!formaPagoSeleccionada()) {
        alert('Seleccioná la forma de pago para calcular los precios.');
        return false;
    }
    const boton = document.getElementById('btnGuardar');
    boton.disabled = true;
    boton.textContent = 'Guardando...';
    return true;
}

document.addEventListener('click', function (event) {
    if (!event.target.closest('#buscarProducto')
            && !event.target.closest('#resultadosProducto')) {
        resultadosProducto.innerHTML = '';
    }
    if (!event.target.closest('#buscarCliente')
            && !event.target.closest('#resultadosCliente')) {
        resultadosCliente.innerHTML = '';
    }
});

renderTabla();
</script>

<style>
#resultadosProducto, #resultadosCliente {
    box-shadow: 0 4px 8px rgba(0, 0, 0, .15);
    border-radius: 4px;
}
.producto-item:hover, .cliente-item:hover {
    background-color: #f0f9ff !important;
}
@media (max-width: 1000px) {
    .col-lg-9, .col-lg-3 {
        flex: 0 0 100%;
        max-width: 100%;
        margin-top: 1rem;
    }
}
</style>
</body>
</html>
