document.getElementById('uploadForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];

    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const content = e.target.result;
            
            // Send the file content to the backend API for processing
            fetch('/process', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ content })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                // Create a blob from the output content
                const blob = new Blob([data.processedContent], { type: 'text/plain' });
                const url = URL.createObjectURL(blob);
                
                // Create a download link
                const link = document.createElement('a');
                link.href = url;
                link.download = 'Processed_TTPs.txt';
                link.click();

                // Clean up
                URL.revokeObjectURL(url);
                document.getElementById('results').innerText = 'File uploaded and processed: ' + file.name;
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('results').innerText = 'There was an error processing the file.';
            });
        };
        reader.readAsText(file);
    } else {
        alert('Please select a file to upload.');
    }
});
