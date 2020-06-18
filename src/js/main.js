let timeoutDuration = 10 // this number should be the duration in seconds
let removeOutputMessageTimeout;

function submitForm() {
    clearTimeout(removeOutputMessageTimeout)

    const fileName = document.getElementById("file").files[0].name;
    const languageCode = document.querySelector("#languageCode").value;
    const dublinCoreId = document.querySelector("#dublinCoreId").value;
    const projectId = document.querySelector("#projectId").value;
    const mediaExtension = document.querySelector("#mediaExtension").value;
    const mediaQuality = document.querySelector("#mediaQuality").value;
    const grouping = document.querySelector("#grouping").value;

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
        document.querySelector(".main-grid__success-message").innerHTML = res.data.output
        document.querySelector(".main-grid__error").classList.remove("main-grid__show-success-status")
        document.querySelector(".main-grid__success").classList.add("main-grid__show-success-status")
    } else {
        document.querySelector(".main-grid__error-message").innerHTML = res.data.output
        document.querySelector(".main-grid__success").classList.remove("main-grid__show-success-status")
        document.querySelector(".main-grid__error").classList.add("main-grid__show-success-status")
    }

    // multiply by 1000 here to convert from seconds to milliseconds
    removeOutputMessageTimeout = setTimeout(removeOutputMessage, 1000 * timeoutDuration)
}

function removeOutputMessage() {
    document.querySelector(".main-grid__success").classList.remove("main-grid__show-success-status")
    document.querySelector(".main-grid__error").classList.remove("main-grid__show-success-status")
}
