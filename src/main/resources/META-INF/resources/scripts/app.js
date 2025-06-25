document.addEventListener('DOMContentLoaded', () => {
    // Drag-and-drop logic for the image upload (on the index.html page)
    const dropArea = document.getElementById('drop-area');
    const fileInput = document.getElementById('fileInput');
    const statusMessage = document.getElementById('statusMessage');

    // Prevent default drag behavior
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    // Highlight drop area when file is dragged over
    ['dragenter', 'dragover'].forEach(eventName => {
        dropArea.addEventListener(eventName, () => dropArea.classList.add('highlight'));
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, () => dropArea.classList.remove('highlight'));
    });

    // Handle file drop
    dropArea.addEventListener('drop', handleDrop);

    function handleDrop(e) {
        let dt = e.dataTransfer;
        let files = dt.files;
        handleFiles(files);
    }

    // Handle file selection when clicking the area
    dropArea.addEventListener('click', () => {
        fileInput.click();
    });

    fileInput.addEventListener('change', () => {
        const files = fileInput.files;
        handleFiles(files);
    });

    function handleFiles(files) {
        const file = files[0];
        if (file && file.type.startsWith('application/pdf')) {
            uploadImage(file);
        } else {
            statusMessage.textContent = 'Please upload a valid PDF file.';
        }
    }

    // Upload image to server
    function uploadImage(file) {
        const formData = new FormData();
        formData.append('mission', file);
         document.getElementById("spinnerOverlay").style.display = "flex";
        // Simulate server upload with Fetch API
        fetch('rag/uploadItem', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                statusMessage.textContent = 'Mission uploaded successfully!';
                // Redirect to form.html after a delay
                response.json()
                .then(data => {
                    document.getElementById("spinnerOverlay").style.display = "none";
                    setTimeout(() => {
                        window.location.href = 'index.html';
                    }, 1000);
                })
            } else {
                statusMessage.textContent = 'Failed to upload Mission.';
            }
        })
        .catch(() => {
            statusMessage.textContent = 'Error occurred while uploading the image.';
        });
    }

});