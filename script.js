// URL da sua API de login
const API_URL = "http://localhost:8080/auth/login";

const form = document.getElementById("form");
const emailInput = document.getElementById("email");
const senhaInput = document.getElementById("senha");

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const email = emailInput.value.trim();
  const senha = senhaInput.value.trim();

  if (!email || !senha) {
    alert("Preencha todos os campos!");
    return;
  }

  try {
    console.log("Enviando requisição para:", API_URL);

    const resp = await fetch(API_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: email,
        senha: senha,
      }),
    });

    console.log("Resposta bruta:", resp);

    if (!resp.ok) {
      const txt = await resp.text();
      console.error("Erro HTTP:", resp.status, txt);
      alert("Email ou senha inválidos!");
      return;
    }

    const data = await resp.json();
    console.log("JSON da resposta:", data);

    if (!data.token) {
      alert("Token não retornado pelo servidor.");
      return;
    }

    localStorage.setItem("token", data.token);
    alert("Login realizado com sucesso!");

    // Se quiser redirecionar:
    window.location.href = "dashboard/dashboard.html";
  } catch (err) {
    console.error("Erro de conexão:", err);
    alert("Erro ao conectar com o servidor.");
  }
});
