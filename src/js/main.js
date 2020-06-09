function submitForm() {

    const fileName = $("#filename").val();
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
$(document).ready( function () {

    $("#submit").click(function() {
        const languageCode = $("#languageCode").val();
        const dublinCoreId = $("#dublinCoreId").val();
        const projectId = $("#projectId").val();
        const mediaExtension = $("#mediaExtension").val();
        const mediaQuality = $("#mediaQuality").val();
        const grouping = $("#grouping").val();
        const filePath = $("#File").val();
        $.ajax({
            url: "http://192.168.1.22:4567/",
            type: "post",
            data: {
                filePath,
                languageCode,
                dublinCoreId,
                projectId,
                mediaExtension,
                mediaQuality,
                grouping
             },
            success: function(res) {
                $("#output").text(res)
            }
        });
    });
});