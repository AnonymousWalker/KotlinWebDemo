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
    if(res.data.success) {
        $("#output").text(res.data.output)
        $(".output-container").removeClass("error")
        $(".output-container").addClass("success")
    } else {
        $("#output").text(res.data.output)
        $(".output-container").removeClass("success")
        $(".output-container").addClass("error")
    }
}
