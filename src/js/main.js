function submitForm() {

    const fileName = $("#file").val();
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
        .then(res => $("#output").text(res.data))
        .catch(err => console.log(err));


}
