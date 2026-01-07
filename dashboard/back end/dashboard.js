const API_BASE = "http://localhost:8080";
const PRODUTO_ENDPOINT = `${API_BASE}/produto`;
const MEUS_ENDPOINT = `${API_BASE}/produto/meus`;
const DELETAR_ENDPOINT = `${API_BASE}/produto/deletar`; // ajuste se seu endpoint for outro

const form = document.getElementById("form-produto");
const nomeInput = document.getElementById("nome");
const descInput = document.getElementById("descricao");
const imagemInput = document.getElementById("imagem");
const precoInput = document.getElementById("preco");
const msgEl = document.getElementById("mensagem");
const container = document.getElementById("produtos");

const token = localStorage.getItem("token");
if (!token) {
  alert("Você não está logado!");
  window.location.href = "../index.html";
}

/* =========================
   RESOLVER URL DA IMAGEM
   ========================= */
function resolveImgSrc(p) {
  const raw =
    p.imgUrl ||
    p.urlImg ||
    p.imagem ||
    p.foto ||
    p.imageUrl ||
    p.imagemPath ||
    "";

  if (!raw) return "./img/sem-imagem.png";

  const str = String(raw).trim();

  if (
    str.startsWith("http://") ||
    str.startsWith("https://") ||
    str.startsWith("data:")
  ) {
    return str;
  }

  if (str.startsWith("/")) return `${API_BASE}${str}`;

  return `${API_BASE}/${str}`;
}

/* =========================
   DELETAR PRODUTO
   ========================= */
async function deletarProduto(id) {
  if (!id) return;

  const ok = confirm("Tem certeza que deseja excluir este produto?");
  if (!ok) return;

  try {
    const resp = await fetch(`${DELETAR_ENDPOINT}/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    });

    const txt = await resp.text();
    console.log("DELETE status:", resp.status, "resp:", txt);

    if (!resp.ok) {
      msgEl.textContent = "Erro ao excluir produto.";
      msgEl.style.color = "red";
      return;
    }

    msgEl.textContent = "Produto excluído com sucesso!";
    msgEl.style.color = "green";
    carregarMeusProdutos();
  } catch (e) {
    console.error(e);
    msgEl.textContent = "Erro ao conectar com o servidor.";
    msgEl.style.color = "red";
  }
}

/* =========================
   CARREGAR MEUS PRODUTOS
   ========================= */
async function carregarMeusProdutos(page = 0) {
  if (!container) return;

  container.innerHTML = "<p>Carregando...</p>";

  try {
    const resp = await fetch(
      `${MEUS_ENDPOINT}?page=${page}&size=10&sort=nome,asc`,
      {
        method: "GET",
        headers: { Authorization: `Bearer ${token}` },
      }
    );

    if (!resp.ok) {
      const txt = await resp.text();
      container.innerHTML = `<p>Erro: ${resp.status} - ${txt}</p>`;
      return;
    }

    const data = await resp.json();
    console.log("Resposta /produto/meus:", data);

    const itens = data.content || [];
    if (itens.length === 0) {
      container.innerHTML = "<p>Ainda não existem produtos cadastrados.</p>";
      return;
    }

    container.innerHTML = itens
      .map(
        (p) => `
        <div class="produto" data-id="${p.id}">
          <img src="${resolveImgSrc(p)}" alt="${p.nome}"/>
          <div class="desc-icon">
            <div class="desc">
              <h3>${p.nome}</h3>
              <p>${p.descricao || ""}</p>
              <strong>R$ ${Number(p.preco).toFixed(2)}</strong>
            </div>

            <div class="icons">
              <!-- ✅ editar: SEM <a>, pega o id e navega com ?id= -->
              <button type="button" class="icone-cate btn-editar" title="Editar">
                <img src="../img/editar.png" class="icon-img" />
              </button>

              <!-- ✅ deletar: classe correta -->
              <button type="button" class="icone-cate btn-deletar" title="Excluir">
                <img src="/img/excluir.png" class="icon-img" />
              </button>
            </div>
          </div>
        </div>
      `
      )
      .join("");
  } catch (e) {
    console.error(e);
    container.innerHTML = "<p>Erro ao carregar produtos.</p>";
  }
}

/* =========================
   CLIQUES (EDITAR / EXCLUIR)
   ========================= */
container.addEventListener("click", (e) => {
  const card = e.target.closest(".produto");
  if (!card) return;

  const id = card.dataset.id;

  // ✅ editar
  const btnEdit = e.target.closest(".btn-editar");
  if (btnEdit) {
    console.log("ID para editar:", id);
    window.location.href = `/editar-produto/editar-produto.html?id=${id}`;
    return;
  }

  // ✅ deletar
  const btnDel = e.target.closest(".btn-deletar");
  if (btnDel) {
    console.log("ID para excluir:", id);
    deletarProduto(id);
    return;
  }
});

// chama assim que a página abrir
carregarMeusProdutos();

/* =========================
   CADASTRAR PRODUTO
   ========================= */
form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const nome = nomeInput.value.trim();
  const descricao = descInput.value.trim();
  const preco = precoInput.value;
  const arquivo = imagemInput.files[0];

  if (!nome || !preco || !arquivo) {
    msgEl.textContent = "Preencha nome, preço e selecione uma imagem.";
    msgEl.style.color = "red";
    return;
  }

  const fd = new FormData();
  fd.append("nome", nome);
  fd.append("descricao", descricao);
  fd.append("preco", preco);
  fd.append("imagem", arquivo);

  try {
    const resp = await fetch(PRODUTO_ENDPOINT, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      body: fd,
    });

    const txt = await resp.text();
    console.log("Status:", resp.status, "Resposta:", txt);

    if (!resp.ok) {
      msgEl.textContent = "Erro ao cadastrar produto.";
      msgEl.style.color = "red";
      return;
    }

    msgEl.textContent = "Produto cadastrado com sucesso!";
    msgEl.style.color = "green";
    form.reset();
    carregarMeusProdutos();
  } catch (err) {
    console.error(err);
    msgEl.textContent = "Erro ao conectar com o servidor.";
    msgEl.style.color = "red";
  }
});
