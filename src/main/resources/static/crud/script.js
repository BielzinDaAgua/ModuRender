function showError(msg) {
    const divErr = document.getElementById('msgError');
    divErr.textContent = msg;
    divErr.classList.remove('d-none'); // exibe
}

// Esconde alerta de erro
function hideError() {
    const divErr = document.getElementById('msgError');
    divErr.classList.add('d-none');   // esconde
}

// Carrega a lista de usuários e exibe na tabela
async function loadUsers() {
    try {
        hideError(); // se havia erro anterior, escondemos
        const resp = await fetch('/api/usuarios', { method: 'GET' });
        if (!resp.ok) {
            throw new Error('Falha ao carregar lista de usuários (status '+resp.status+')');
        }
        const userList = await resp.json();  // supõe que retorne JSON
        renderUsers(userList);
    } catch (err) {
        showError(err.message);
    }
}

// Renderiza a lista de usuários na tabela
function renderUsers(users) {
    const tbody = document.getElementById('usuariosTableBody');
    tbody.innerHTML = ''; // limpa antes
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

// Exclui usuário e recarrega a lista
async function deleteUser(id) {
    if (!confirm('Tem certeza que deseja excluir este usuário?')) return;
    try {
        hideError();
        const resp = await fetch('/api/usuarios/' + id, {
            method: 'DELETE'
        });
        if (!resp.ok) {
            const txt = await resp.text();
            throw new Error(`Erro ao excluir usuário:\n${txt}`);
        }
        // se deu certo
        await loadUsers(); // recarrega a tabela
    } catch (err) {
        showError(err.message);
    }
}

// Lida com envio de formulário
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
        const response = await fetch('/api/usuarios', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuario)
        });

        if (response.ok) {
            alert('Usuário criado com sucesso!');
            // recarrega lista de usuários
            await loadUsers();
            // limpa campos do form
            document.getElementById('userForm').reset();
        } else {
            const errorText = await response.text();
            showError(`Erro ao criar usuário:\n${errorText}`);
        }
    } catch (err) {
        showError(err.message);
    }
});

// Quando a página carrega, busca e exibe a lista
document.addEventListener('DOMContentLoaded', loadUsers);