const paymentRepository = require('../repositories/paymentRepository');

const findAll = async ({ page, size, sort, customerId }) => {
  const [payments, countResult] = await Promise.all([
    paymentRepository.findAll({ page, size, sort, customerId }),
    paymentRepository.count({ customerId }),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: payments, total, page, size };
};

const findById = async (paymentId) => {
  const payment = await paymentRepository.findById(paymentId);
  if (!payment) {
    const err = new Error(`Payment with id ${paymentId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return payment;
};

const findByIdWithDetails = async (paymentId) => {
  const payment = await paymentRepository.findByIdWithDetails(paymentId);
  if (!payment) {
    const err = new Error(`Payment with id ${paymentId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return payment;
};

const update = async (paymentId, data) => {
  await findById(paymentId);
  const [payment] = await paymentRepository.update(paymentId, data);
  return payment;
};

const remove = async (paymentId) => {
  await findById(paymentId);
  await paymentRepository.remove(paymentId);
};

module.exports = { findAll, findById, findByIdWithDetails, update, remove };
