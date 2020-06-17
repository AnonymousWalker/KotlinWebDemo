function submitForm() {
    const fileName = document.getElementById("file").files[0].name;
    const languageCode = $("#languageCode").val();
    const dublinCoreId = $("#dublinCoreId").val();
    const projectId = $("#projectId").val();
    const mediaExtension = $("#mediaExtension").val();
    const mediaQuality = $("#mediaQuality").val();
    const grouping = $("#grouping").val();

    axios.post('http://localhost:4567/', {
        fileName,
        languageCode,
        dublinCoreId,
        projectId,
        mediaExtension,
        mediaQuality,
        grouping
    })
        .then(res => handleResponse(res))
        .catch(err => console.log(err));
}

function handleResponse(res) {
    $(".main-grid__output-text").text(res.data.output)
    if(res.data.success) {
        $(".main-grid__output").removeClass("main-grid__output--error").addClass("main-grid__output--success")
    } else {
        $(".main-grid__output").removeClass("main-grid__output--success").addClass("main-grid__output--error")
    }
}
