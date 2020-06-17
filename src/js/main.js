let timeoutDuration = 10 // this number should be the duration in seconds
let removeOutputMessageTimeout;

function submitForm() {
    clearTimeout(removeOutputMessageTimeout)

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
    $(".main-grid__output-message").text(res.data.output)
    if(res.data.success) {
        $(".main-grid__success-status").text("Success!")
        $(".main-grid__output").removeClass("main-grid__output--error").addClass("main-grid__output--success")
    } else {
        $(".main-grid__success-status").text("Error!")
        $(".main-grid__output").removeClass("main-grid__output--success").addClass("main-grid__output--error")
    }

    // multiply by 1000 here to convert from seconds to milliseconds
    removeOutputMessageTimeout = setTimeout(removeOutputMessage, 1000 * timeoutDuration)
}

function removeOutputMessage() {
    $(".main-grid__output").removeClass("main-grid__output--success").removeClass("main-grid__output--error")
}
