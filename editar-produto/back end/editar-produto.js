const API_BASE = "http://localhost:8080";
const PRODUTO_ENDPOINT = `${API_BASE}/produto`;
const MEUS_ENDPOINT = `${API_BASE}/produto/meus`;

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

  if (!raw) return "/CATALOGUEI MATHEUS/img/img frame.png";

  const str = String(raw).trim();

  // se já for http/https ou data: usa direto
  if (
    str.startsWith("http://") ||
    str.startsWith("https://") ||
    str.startsWith("data:")
  ) {
    return str;
  }

  // se começar com /, prefixa com API_BASE
  if (str.startsWith("/")) {
    return `${API_BASE}${str}`;
  }

  // qualquer outra coisa: assume relativo ao backend
  return `${API_BASE}/${str}`;
}

let produtoEditandoId = null;

window.prepararEdicao = (id, nome, descricao, preco) => {
  produtoEditandoId = id;
  nomeInput.value = nome || "";
  descInput.value = descricao || "";
  precoInput.value = preco ?? "";
  msgEl.textContent = `Editando produto #${id}`;
  msgEl.style.color = "#333";
};

// =======================
// CARREGAR MEUS PRODUTOS
// =======================
async function carregarMeusProdutos(page = 0) {
  if (!container) return;

  container.innerHTML = "<p>Carregando...</p>";

  try {
    const resp = await fetch(
      `${MEUS_ENDPOINT}?page=${page}&size=10&sort=nome,asc`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!resp.ok) {
      const txt = await resp.text();
      container.innerHTML = `<p>Erro: ${resp.status} - ${txt}</p>`;
      return;
    }

    const data = await resp.json();
    console.log("Resposta /produto/meus:", data); // 👀 pra você ver o JSON
    const itens = data.content || [];

    if (itens.length === 0) {
      container.innerHTML = "<p>Você ainda não cadastrou produtos.</p>";
      return;
    }

    container.innerHTML = itens
      .map(
        (p) => `
    <div class="produto" data-id="${p.id}">
      <img src="${resolveImgSrc(p)}" alt="${p.nome}" />
      <h3>${p.nome}</h3>
      <p>${p.descricao || ""}</p>
      <strong>R$ ${Number(p.preco).toFixed(2)}</strong>

      <button type="button" class="btn-editar">Editar</button>
      <button type="button" class="btn-cancelar" style="display:none">Cancelar</button>
    </div>
  `
      )
      .join("");
  } catch (e) {
    console.error(e);
    container.innerHTML = "<p>Erro ao carregar produtos.</p>";
  }
}

// chama assim que a página abrir
carregarMeusProdutos();
container.addEventListener("click", (e) => {
  const card = e.target.closest(".produto");
  if (!card) return;

  const id = card.dataset.id;

  // EDITAR
  if (e.target.closest(".btn-editar")) {
    const nome = card.querySelector("h3")?.textContent || "";
    const descricao = card.querySelector("p")?.textContent || "";
    const precoTxt = card.querySelector("strong")?.textContent || "0";
    const preco = precoTxt.replace("R$", "").trim().replace(",", ".");

    produtoEditandoId = id;
    nomeInput.value = nome;
    descInput.value = descricao;
    precoInput.value = preco;

    msgEl.textContent = `Editando produto #${id}`;
    msgEl.style.color = "#333";

    // mostra cancelar
    card.querySelector(".btn-cancelar").style.display = "inline-block";
    return;
  }

  // CANCELAR
  if (e.target.closest(".btn-cancelar")) {
    produtoEditandoId = null;
    form.reset();
    msgEl.textContent = "";
    e.target.closest(".btn-cancelar").style.display = "none";
    return;
  }
});

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const nome = nomeInput.value.trim();
  const descricao = descInput.value.trim();
  const preco = precoInput.value;
  const arquivo = imagemInput.files[0]; // imagem opcional na edição

  if (!nome || !preco) {
    msgEl.textContent = "Preencha nome e preço.";
    msgEl.style.color = "red";
    return;
  }

  const fd = new FormData();
  fd.append("nome", nome);
  fd.append("descricao", descricao);
  fd.append("preco", preco);

  // ✅ só manda imagem se o usuário selecionou uma nova
  if (arquivo) fd.append("imagem", arquivo);

  try {
    // ✅ aqui decide: editar ou cadastrar
    const url = produtoEditandoId
      ? `${API_BASE}/produto/editar/${produtoEditandoId}` // ou /produto/${id}
      : `${API_BASE}/produto`;

    const method = produtoEditandoId ? "PUT" : "POST";

    const resp = await fetch(url, {
      method,
      headers: { Authorization: `Bearer ${token}` },
      body: fd,
    });

    const txt = await resp.text();
    console.log("Status:", resp.status, "Resposta:", txt);

    if (!resp.ok) {
      msgEl.textContent = "Erro ao salvar produto.";
      msgEl.style.color = "red";
      return;
    }

    msgEl.textContent = produtoEditandoId
      ? "Produto editado com sucesso!"
      : "Produto cadastrado com sucesso!";
    msgEl.style.color = "green";

    form.reset();
    produtoEditandoId = null;
    carregarMeusProdutos();
  } catch (err) {
    console.error(err);
    msgEl.textContent = "Erro ao conectar com o servidor.";
    msgEl.style.color = "red";
  }
});
