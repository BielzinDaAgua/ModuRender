function inserir(num) {
    document.getElementById('resultado').value += num;
}

function operar(op) {
    const resultado = document.getElementById('resultado');
    if (resultado.value === "" || /[+\-*/]$/.test(resultado.value)) return;
    resultado.value += op;
}

function limpar() {
    document.getElementById('resultado').value = '';
}

function calcular() {
    try {
        const resultado = eval(document.getElementById('resultado').value);
        document.getElementById('resultado').value = resultado;
    } catch (e) {
        document.getElementById('resultado').value = "Erro";
    }
}
