const reportService = require('../services/reportService');

const salesByCategory = async (req, res, next) => {
  try {
    const result = await reportService.salesByCategory();
    res.json(result);
  } catch (err) { next(err); }
};

const salesByStore = async (req, res, next) => {
  try {
    const result = await reportService.salesByStore();
    res.json(result);
  } catch (err) { next(err); }
};

module.exports = { salesByCategory, salesByStore };
