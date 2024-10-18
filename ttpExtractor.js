const predefinedStatements = {
    "Credential Dumping": `
    Description: Attackers often extract sensitive information like usernames and passwords from operating systems or applications.
    Example Sources: LSASS process, SAM database, Windows registry.
    `,
    "Spear Phishing": `
    Description: Targeted phishing attacks aimed at specific individuals or organizations to steal credentials or deliver malware.
    Example Sources: Emails with malicious attachments or links.
    `,
    "Command and Control": `
    Description: Attackers use encrypted channels (like HTTPS) to communicate with compromised systems, making detection difficult.
    Example Sources: Encrypted traffic to a remote server.
    `,
    // Add more predefined statements as needed
};

const extractTTPs = (content) => {
    if (!content) throw new Error('No content provided');
    console.log('Processing content...');

    const ttpSummary = [];

    for (const [keyword, statement] of Object.entries(predefinedStatements)) {
        const regex = new RegExp(`\\b${keyword}\\b`, 'i');
        if (regex.test(content)) {
            ttpSummary.push(`
            ${statement}
            `);
        }
    }

    if (ttpSummary.length === 0) {
        return 'No TTPs found in the document.';
    }

    return ttpSummary.join('\n');
};

module.exports = { extractTTPs };
