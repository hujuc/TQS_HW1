async function loadCheckInForm() {
    const content = document.getElementById('content');
    content.innerHTML = `
        <div class="form-container">
            <h1 class="mb-4">Check-in</h1>

            <form class="mb-4" onsubmit="checkIn(event)">
                <div class="row">
                    <div class="col-md-8">
                        <div class="input-group">
                            <input type="text" class="form-control" id="reservationCode" 
                                   placeholder="Digite o código da reserva" required>
                            <button class="btn btn-primary" type="submit">Verificar</button>
                        </div>
                    </div>
                </div>
            </form>

            <div id="checkInDetails" class="card d-none">
                <div class="card-body">
                    <h5 class="card-title">Detalhes da Reserva</h5>
                    <div id="checkInContent"></div>
                </div>
            </div>
        </div>
    `;
}

async function checkIn(event) {
    event.preventDefault();
    const code = document.getElementById('reservationCode').value;

    try {
        const reservation = await reservationsApi.getByCode(code);
        const checkInDetails = document.getElementById('checkInDetails');
        const checkInContent = document.getElementById('checkInContent');
        
        checkInDetails.classList.remove('d-none');
        checkInContent.innerHTML = `
            <p class="card-text">
                <strong>Código:</strong> ${reservation.reservationCode}<br>
                <strong>Cliente:</strong> ${reservation.customerName}<br>
                <strong>Refeição:</strong> ${reservation.meal.name}<br>
                <strong>Data:</strong> ${formatDate(reservation.meal.date)}<br>
                <strong>Hora:</strong> ${reservation.meal.type}<br>
                <strong>Número de Pessoas:</strong> ${reservation.numberOfPeople}<br>
                <strong>Status:</strong> 
                <span class="reservation-status ${reservation.status.toLowerCase()}">
                    ${reservation.status === 'CONFIRMED' ? 'Confirmada' : 
                      reservation.status === 'CANCELLED' ? 'Cancelada' : 'Pendente'}
                </span>
            </p>
            
            ${reservation.status === 'CONFIRMED' ? `
                <form onsubmit="confirmCheckIn(event, '${reservation.reservationCode}')">
                    <button type="submit" class="btn btn-success">Confirmar Check-in</button>
                </form>
            ` : ''}
        `;
    } catch (error) {
        showError('Reserva não encontrada');
        document.getElementById('checkInDetails').classList.add('d-none');
    }
}

async function confirmCheckIn(event, code) {
    event.preventDefault();
    
    try {
        const checkedInReservation = await reservationsApi.checkIn(code);
        showMessage('Check-in realizado com sucesso');
        await checkIn(null, code);
    } catch (error) {
        showError('Erro ao realizar check-in');
    }
} 