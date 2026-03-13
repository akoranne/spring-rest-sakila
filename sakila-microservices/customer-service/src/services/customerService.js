const customerRepository = require('../repositories/customerRepository');
const httpClient = require('../utils/httpClient');
const config = require('../config');

const findAll = async ({ page, size, sort }) => {
  const [customers, countResult] = await Promise.all([
    customerRepository.findAll({ page, size, sort }),
    customerRepository.count(),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: customers, total, page, size };
};

const findById = async (customerId) => {
  const customer = await customerRepository.findById(customerId);
  if (!customer) {
    const err = new Error(`Customer with id ${customerId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return customer;
};

const create = async (data) => {
  const [customer] = await customerRepository.create(data);
  return customer;
};

const update = async (customerId, data) => {
  await findById(customerId);
  const [customer] = await customerRepository.update(customerId, data);
  return customer;
};

const remove = async (customerId) => {
  await findById(customerId);
  await customerRepository.remove(customerId);
};

const findByIdWithDetails = async (customerId, { correlationId, authToken } = {}) => {
  const customer = await findById(customerId);

  const paymentUrl = `${config.paymentServiceUrl}/payments?customerId=${customerId}`;
  const rentalUrl = `${config.rentalServiceUrl}/rentals?customerId=${customerId}`;

  const opts = { correlationId, authToken };

  let paymentResponse, rentalResponse;
  try {
    [paymentResponse, rentalResponse] = await Promise.all([
      httpClient.get(paymentUrl, opts),
      httpClient.get(rentalUrl, opts),
    ]);
  } catch (err) {
    if (err.statusCode === 503 || err.code === 'SERVICE_UNAVAILABLE') {
      const serviceErr = new Error(err.message || 'Downstream service unavailable');
      serviceErr.statusCode = 503;
      serviceErr.code = 'SERVICE_UNAVAILABLE';
      throw serviceErr;
    }
    throw err;
  }

  return {
    ...customer,
    payments: paymentResponse.data.data || [],
    rentals: rentalResponse.data.data || [],
  };
};

module.exports = { findAll, findById, findByIdWithDetails, create, update, remove };
