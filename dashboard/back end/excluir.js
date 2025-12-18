async function deletarProduto(id) {
  const ok = confirm("Tem certeza que deseja excluir este produto?");
  if (!ok) return;

  try {
    const resp = await fetch(`${PRODUTO_ENDPOINT}/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!resp.ok) {
      const txt = await resp.text();
      alert(`Erro ao excluir: ${resp.status} - ${txt}`);
      return;
    }

    // Recarrega a lista após excluir
    carregarMeusProdutos();
  } catch (e) {
    console.error(e);
    alert("Erro de conexão ao excluir.");
  }
}
