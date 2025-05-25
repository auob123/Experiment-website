let model;
let currentStep = 1;
let selectedExperiment = null;

async function startWebcam() {
    const video = document.getElementById('webcam');
    try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true });
        video.srcObject = stream;
    } catch (error) {
        document.getElementById('feedback').innerText = 'Error: Allow camera access.';
        console.error("Webcam error:", error);
    }
}

async function loadModel() {
    try {
        model = await cocoSsd.load();
        console.log("Model loaded");
    } catch (error) {
        console.error("Model loading error:", error);
        document.getElementById('feedback').innerText = 'Error loading AI model.';
    }
}

async function detectFrame() {
    const video = document.getElementById('webcam');
    const canvas = document.getElementById('canvas');
    const context = canvas.getContext('2d');
    context.drawImage(video, 0, 0, canvas.width, canvas.height);
    return await model.detect(canvas);
}

function detectFlame(canvas) {
    const context = canvas.getContext('2d');
    const imageData = context.getImageData(0, 0, canvas.width, canvas.height);
    const data = imageData.data;
    let flamePixels = 0;
    for (let i = 0; i < data.length; i += 4) {
        const r = data[i], g = data[i + 1], b = data[i + 2];
        if (r > 150 && g > 100 && b < 100) flamePixels++;
    }
    return flamePixels > 1000;
}

async function guideStudent() {
    if (!selectedExperiment) {
        document.getElementById('feedback').innerText = 'Please select an experiment first.';
        return;
    }

    try {
        const predictions = await detectFrame();
        const canvas = document.getElementById('canvas');
        let prompt = `Experiment: ${selectedExperiment.title}. Current step: ${currentStep}. Instruction: ${selectedExperiment.instructions[currentStep - 1]}. Detected objects: ${JSON.stringify(predictions)}. `;
        
        if (selectedExperiment.slug === "air-cannon" && currentStep === 4) {
            const hasFlame = detectFlame(canvas);
            prompt += `Flame detected: ${hasFlame}. `;
        }
        
        prompt += `Guide the student through this step and check if it's correct.`;

        const response = await fetch('https://api.deepseek.com/v1/chat/completions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer sk-b448a0c6eae24e66b72a9876eb0e93ad' // Replace with your actual key
            },
            body: JSON.stringify({
                model: 'deepseek-v3',
                messages: [{ role: 'user', content: prompt }],
                max_tokens: 200
            })
        });
        
        const data = await response.json();
        const feedback = data.choices[0].message.content;
        document.getElementById('feedback').innerText = feedback;
        
        if (feedback.includes('correct') || feedback.includes('good')) {
            currentStep = Math.min(currentStep + 1, selectedExperiment.instructions.length);
        }
    } catch (error) {
        console.error("API error:", error);
        document.getElementById('feedback').innerText = 'Error contacting AI service.';
    }
}

function updateUI() {
    const expSelected = !!selectedExperiment;
    document.getElementById('instructions').style.display = expSelected ? 'block' : 'none';
    document.querySelector('.ai-guide').style.display = expSelected ? 'block' : 'none';
    document.getElementById('experiment-status').style.display = expSelected ? 'inline-block' : 'none';
    
    if (expSelected) {
        const instructionList = document.getElementById('instruction-list');
        instructionList.innerHTML = '';
        selectedExperiment.instructions.forEach(instruction => {
            const li = document.createElement('li');
            li.textContent = instruction;
            instructionList.appendChild(li);
        });
    }
}

function updateExperimentDetails() {
    const select = document.getElementById('experiment-select');
    const value = select.value;
    
    if (value && experiments[value]) {
        selectedExperiment = experiments[value];
        currentStep = 1;
        updateUI();
        window.location.href = `/experiment-guide?exp=${value}`;
    }
}

window.onload = async () => {
    try {
        // Initialize from URL parameter if present
        const urlParams = new URLSearchParams(window.location.search);
        const expParam = urlParams.get('exp');
        
        if (expParam && experiments[expParam]) {
            selectedExperiment = experiments[expParam];
            document.getElementById('experiment-select').value = expParam;
        }
        
        await startWebcam();
        await loadModel();
        updateUI();
        
        document.getElementById('start-guide').addEventListener('click', () => {
            setInterval(guideStudent, 5000);
        });
    } catch (error) {
        console.error("Initialization failed:", error);
        document.getElementById('feedback').innerText = "Failed to initialize. Check console for details.";
    }
};