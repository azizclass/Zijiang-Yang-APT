var template = '<tr class="file_preview">\
    <td class="preview_image_td">\
        <img class="preview_image" data-dz-thumbnail />\
    </td>\
    <td class="file_info_td">\
        <div class="file_info" data-dz-name></div>\
        <div class="file_info" data-dz-size></div>\
    </td>\
    <td class="progree_td">\
        <div class="progress_bar_container">\
            <img class="progress_bar" data-dz-uploadprogress src="/assets/images/progressbar.gif">\
        </div>\
    </td>\
    <td class="bt_td bt_upload_td">\
        <button class="button upload_bt">Upload</button>\
        <img class="uploading_sign" src="/assets/images/loading.gif" hidden>\
        </td>\
    <td class="bt_td"><button class="button remove_bt" data-dz-remove>Remove</button></td>\
</tr>';

var check= '<i class="fa fa-check fa-3x" style="color: rgba(88, 191, 29, 0.65);"></i>';
var fail = '<i class="fa fa-times fa-3x" style="color: red"></i>';



$(function() {
    var myDropzone = new Dropzone(document.body, {
        url: 'Generated dynamically',
        paramName: "img", // The name that will be used to transfer the file
        previewTemplate: template,
        previewsContainer: '#preview_table',
        clickable: '#bt_choose_files',
        autoProcessQueue: false,
        parallelUploads: 1,
        filesizeBase: 1024,
        init: function() {

            this.busy = false;
            this.uploadQueue = new Queue();
            this.startUpload = function (file) {
                var dropzone = this;
                $.ajax({
                    type: 'POST',
                    url: window.location.href,
                    dataType: 'text',
                    data: {
                        upload: 'true'
                    },
                    success: function (text, status, request) {
                        dropzone.options.url = text;
                    },
                    error: function () {
                        console.log("There is an error!");
                    },

                    complete: function(request, status){
                        dropzone.processFile(file);
                    }
                });
            };
            this.upload = function(file){
                if(file.waitingForUpload) return;
                file.waitingForUpload = true;
                this.uploadQueue.enqueue(file);
                file.previewElement.querySelector('.upload_bt').setAttribute(['hidden'], [true]);
                file.previewElement.querySelector('.remove_bt').setAttribute(['hidden'], [true]);
                file.previewElement.querySelector('.uploading_sign').removeAttribute(['hidden']);
                if(!this.busy){
                    this.busy = true;
                    this.startUpload(this.uploadQueue.dequeue());
                }
            };

            this.on('addedfile', function(file) {
                var dropzone = this;
                file.waitingForUpload = false;
                file.previewElement.querySelector('.upload_bt').addEventListener('click', function () {
                    dropzone.upload(file);
                });
            });

            this.on('success', function(file){
                file.previewElement.querySelector('.bt_upload_td').appendChild(Dropzone.createElement(check));
            });

            this.on('error', function(file, errorMessage){
                file.previewElement.querySelector('.bt_upload_td').appendChild(Dropzone.createElement(fail));
                file.previewElement.querySelector('.progress_bar').setAttribute(['hidden'], [true]);
            });

            this.on('complete', function (file) {
                file.previewElement.querySelector('.uploading_sign').setAttribute(['hidden'], [true]);
                file.waitingForUpload = false;
                if (!this.uploadQueue.isEmpty()) {
                    this.startUpload(this.uploadQueue.dequeue());
                } else
                    this.busy = false;
            });
        }
    });



    $('#bt_upload_all').click(function(){
        var files = myDropzone.getQueuedFiles();
        for(var i=0; i<files.length; i++)
            myDropzone.upload(files[i]);
    });

    $('#bt_cancel_all').click(function(){
        while(!myDropzone.uploadQueue.isEmpty()) {
            var file = myDropzone.uploadQueue.dequeue();
            file.waitingForUpload = false;
            file.previewElement.querySelector('.uploading_sign').setAttribute(['hidden'], [true]);
            file.previewElement.querySelector('.upload_bt').removeAttribute(['hidden']);
            file.previewElement.querySelector('.remove_bt').removeAttribute(['hidden']);
        }
    });
});
