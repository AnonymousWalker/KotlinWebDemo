document.addEventListener('submit', e => {
    const form = e.target
    e.preventDefault()

    fetch(form.action, {
        method: form.method,
        body: new FormData(form)
    })
        .then(res => console.log(res)) // TODO: handle the response appropriately
})

function handleResponse(res) {
    $(".main-grid__output-text").text(res.data.output)
    if(res.data.success) {
        $(".main-grid__output").removeClass("main-grid__output--error").addClass("main-grid__output--success")
    } else {
        $(".main-grid__output").removeClass("main-grid__output--success").addClass("main-grid__output--error")
    }
}