    let operacao = "";

    function inserir(num) {
        document.getElementById('resultado').value += num;
    }

    function operar(op) {
        operacao = op;
        document.getElementById('resultado').value += op;
    }

    function limpar() {
        operacao = "";
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
