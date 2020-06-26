const timeoutDuration = 5000 // 5 seconds
let removeOutputMessageTimeout

document.addEventListener('submit', e => {
    clearTimeout(removeOutputMessageTimeout)

    const form = e.target
    e.preventDefault()

    fetch(form.action, {
        method: form.method,
        body: new FormData(form)
    })
        .then(res => res.json())
        .then(data => handleResponse(data))
})

function handleResponse(data) {
    if(data.success) {
        showSuccess(data)
    } else {
        showError(data)
    }

    removeOutputMessageTimeout = setTimeout(removeOutputMessage, timeoutDuration)
}

function showSuccess(data) {
    document.querySelector(".main-grid__success-message").innerHTML = `File placed at ${data.output}`
    document.querySelector(".main-grid__error").classList.remove("main-grid__show-success-status")
    document.querySelector(".main-grid__success").classList.add("main-grid__show-success-status")
    document.querySelector(".main-grid__success").style.display = "block";
}

function showError(data) {
    document.querySelector(".main-grid__error-message").innerHTML = data.output
    document.querySelector(".main-grid__success").classList.remove("main-grid__show-success-status")
    document.querySelector(".main-grid__error").classList.add("main-grid__show-success-status")
    document.querySelector(".main-grid__error").style.display = "block"
}

function removeOutputMessage() {
    document.querySelector(".main-grid__success").classList.remove("main-grid__show-success-status")
    document.querySelector(".main-grid__error").classList.remove("main-grid__show-success-status")
    document.querySelector(".main-grid__success, .main-grid__error").style.display = "none"
}
