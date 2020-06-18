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
    if(res.data.success) {
        $(".main-grid__success-message").text(res.data.output)
        $(".main-grid__error").removeClass("main-grid__show-success-status")
        $(".main-grid__success").addClass("main-grid__show-success-status")
    } else {
        $(".main-grid__error-message").text(res.data.output)
        $(".main-grid__success").removeClass("main-grid__show-success-status")
        $(".main-grid__error").addClass("main-grid__show-success-status")
    }

    // multiply by 1000 here to convert from seconds to milliseconds
    removeOutputMessageTimeout = setTimeout(removeOutputMessage, 1000 * timeoutDuration)
}

function removeOutputMessage() {
    $(".main-grid__success").removeClass("main-grid__show-success-status")
    $(".main-grid__error").removeClass("main-grid__show-success-status")
}
