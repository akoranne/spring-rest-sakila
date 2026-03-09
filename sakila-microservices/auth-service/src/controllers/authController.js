const authService = require('../services/authService');
const { loginSchema } = require('../validators/authValidator');

const login = async (req, res, next) => {
  try {
    const { error, value } = loginSchema.validate(req.body, { abortEarly: false });

    if (error) {
      const details = error.details.map((d) => ({
        field: d.context.key,
        message: d.message,
      }));

      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Request validation failed',
          details,
          timestamp: new Date().toISOString(),
        },
      });
    }

    const token = await authService.login(value.email, value.password);

    res.set('Authorization', `Bearer ${token}`);
    res.json({ message: 'Login successful' });
  } catch (err) {
    next(err);
  }
};

module.exports = { login };
