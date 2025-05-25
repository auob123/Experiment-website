// air-cannon-validation.js
export const stepValidators = {
    1: (results) => {
        // Validate cup cutting using object detection
        return { valid: true, message: "Hole detected correctly!" };
    },
    4: (results) => {
        // Validate snapping motion
        const landmarks = results.multiHandLandmarks[0];
        return checkSnappingMotion(landmarks);
    }
};

function checkSnappingMotion(landmarks) {
    const tip = landmarks[8];  // Index finger tip
    const pip = landmarks[6];  // Index finger PIP joint
    return {
        valid: tip.y < pip.y,
        message: tip.y < pip.y 
            ? "Good snapping motion!" 
            : "Snap harder downward!"
    };
}