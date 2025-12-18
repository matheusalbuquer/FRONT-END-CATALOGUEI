const API_URL = "http://localhost:8080/produto/lojas";

// nome EXATO da loja
const nomeLoja = "triade store";

const token = localStorage.getItem("token");

if (!token) {
  alert("Você não está logado!");
  window.location.href = "../index.html";
}

async function carregarProdutos() {
  const container = document.getElementById("produtos");

  try {
    const resp = await fetch(`${API_URL}/${nomeLoja}/produtos`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!resp.ok) {
      const txt = await resp.text();
      container.innerHTML = `<p>Erro: ${txt}</p>`;
      return;
    }

    const data = await resp.json();
    console.log("Resposta da API:", data);

    container.innerHTML = "";

    if (!data.content || data.content.length === 0) {
      container.innerHTML = "<p>Nenhum produto encontrado.</p>";
      return;
    }

    data.content.forEach((prod) => {
      console.log("Produto:", prod);

      // tenta vários nomes de campo:
      const rawImg =
        prod.imgUrl || // se veio como imgUrl
        prod.urlImg || // se veio como urlImg
        prod.imagem || // se vier como imagem
        "";

      let urlImagem;

      if (!rawImg) {
        // sem imagem → placeholder
        urlImagem = "https://via.placeholder.com/200x180?text=Sem+Imagem";
      } else if (rawImg.startsWith("http")) {
        // já veio com URL completa
        urlImagem = rawImg;
      } else {
        // veio só o caminho tipo "/uploads/..."
        urlImagem = `http://localhost:8080${
          rawImg.startsWith("/") ? rawImg : "/" + rawImg
        }`;
      }

      const card = document.createElement("div");
      card.classList.add("card");

      card.innerHTML = `
        <img src="${urlImagem}" alt="${prod.nome}">
        <strong>${prod.nome}</strong>
        <div class="price">Preço: R$ ${prod.preco}</div>
        <div class="desc">${prod.descricao ?? "Sem descrição"}</div>
      `;

      container.appendChild(card);
    });
  } catch (err) {
    console.error("Erro:", err);
    container.innerHTML = "<p>Erro ao carregar produtos.</p>";
  }
}

carregarProdutos();
