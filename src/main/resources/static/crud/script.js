function showError(msg) {
    const divErr = document.getElementById('msgError');
    divErr.textContent = msg;
    divErr.classList.remove('d-none');
}

function hideError() {
    const divErr = document.getElementById('msgError');
    divErr.classList.add('d-none');
}

async function loadUsers() {
    try {
        hideError();

        const resp = await fetch('/usuarios', { method: 'GET' });
        if (!resp.ok) {
            throw new Error('Falha ao carregar lista de usuários (status ' + resp.status + ')');
        }

        const users = await resp.json(); // Agora SEMPRE será um array válido
        renderUsers(users);
    } catch (err) {
        showError(err.message);
    }
}

function renderUsers(users) {
    const tbody = document.getElementById('usuariosTableBody');
    tbody.innerHTML = '';
    users.forEach(u => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${u.nome}</td>
            <td>${u.email}</td>
            <td>${u.tipoUsuario}</td>
            <td><button class="btn btn-sm btn-danger" onclick="deleteUser(${u.id})">Excluir</button></td>
        `;
        tbody.appendChild(tr);
    });
}

async function deleteUser(id) {
    if (!confirm('Tem certeza que deseja excluir este usuário?')) return;
    try {
        hideError();
        const resp = await fetch('/usuarios/' + id, { method: 'DELETE' });
        if (!resp.ok) {
            const txt = await resp.text();
            throw new Error(`Erro ao excluir usuário:\n${txt}`);
        }
        await loadUsers();
    } catch (err) {
        showError(err.message);
    }
}

document.getElementById('userForm').addEventListener('submit', async function (event) {
    event.preventDefault();
    hideError();

    const usuario = {
        nome: document.getElementById('nome').value,
        email: document.getElementById('email').value,
        senha: document.getElementById('senha').value,
        tipoUsuario: document.getElementById('tipoUsuario').value
    };

    try {
        const response = await fetch('/usuarios', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuario)
        });

        if (response.ok) {
            alert('Usuário criado com sucesso!');
            await loadUsers();
            document.getElementById('userForm').reset();
        } else {
            const errorText = await response.text();
            showError(`Erro ao criar usuário:\n${errorText}`);
        }
    } catch (err) {
        showError(err.message);
    }
});

document.addEventListener('DOMContentLoaded', loadUsers);
