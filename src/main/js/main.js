function submitForm() {

    const languageCode = $("#languageCode").val();
    const dublinCoreId = $("#dublinCoreId").val();
    const projectId = $("#projectId").val();
    const mediaExtenstion = $("#mediaExtension").val();
    const mediaQuality = $("#mediaQuality").val();
    const grouping = $("#grouping").val();

    axios.post('http://localhost:4567/', {
        languageCode,
        dublinCoreId,
        projectId,
        mediaExtenstion,
        mediaQuality,
        grouping
    })
        .then(res => console.log(res))
        .catch(err => console.log(err));

}
