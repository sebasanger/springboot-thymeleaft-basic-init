const topbar = document.getElementById("topbar");

const loginModal = document.getElementById("login-modal");
const registerModal = document.getElementById("register-modal");

const loginForm = document.getElementById("login-form");
const registerForm = document.getElementById("register-form");

if (topbar) {
  topbar.addEventListener("click", (e) => {
    if (e.target.parentElement.dataset.type != undefined) {
      if (e.target.parentElement.dataset.type == "login") {
        loginModal.classList.add("--show");
      } else if ((e.target.parentElement.dataset.type = "register")) {
        registerModal.classList.add("--show");
      }
    }
  });
}

if (loginModal) {
  loginModal.addEventListener("click", (e) => {
    if (e.target.classList.contains("--show")) {
      loginModal.classList.remove("--show");
    }
  });
}

if (loginForm) {
  loginForm.addEventListener("submit", (e) => {
    setTimeout(() => {
      e.target.button.blur();
    }, 200);

    e.preventDefault();
  });
}

if (registerModal) {
  registerModal.addEventListener("click", (e) => {
    if (e.target.classList.contains("--show")) {
      registerModal.classList.remove("--show");
    }
  });
}

if (registerForm) {
  registerForm.addEventListener("submit", (e) => {
    setTimeout(() => {
      e.target.button.blur();
    }, 200);
    e.preventDefault();
  });
}
