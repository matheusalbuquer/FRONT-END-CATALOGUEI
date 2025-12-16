function cancelarCategoria() {
    document.querySelector('.msg').style.display = 'flex';
    document.querySelector('.criando-cate-conteiner').style.display = 'none';
    document.querySelector('.add-cate').style.display = 'block';
    document.querySelector('.cate-products-container').style.display = 'none'
}

function abrirCriarCategoria() {
    document.querySelector('.msg').style.display = 'none';
    document.querySelector('.criando-cate-conteiner').style.display = 'flex';
    document.querySelector('.cate-products-container').style.display = 'block';
}

document.getElementById('cancelar-cate').addEventListener('click', cancelarCategoria);
document.getElementById('add-cate').addEventListener('click', abrirCriarCategoria);